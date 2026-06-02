package ru.practicum.ewm.event.service.event;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.mapper.CategoryMapper;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.category.CategoryDao;
import ru.practicum.ewm.event.repository.event.EventDao;
import ru.practicum.ewm.event.repository.event.EventQdslDao;
import ru.practicum.ewm.interaction.client.request.RequestClient;
import ru.practicum.ewm.interaction.client.user.UserClient;
import ru.practicum.ewm.interaction.dto.category.CategoryDto;
import ru.practicum.ewm.interaction.dto.event.*;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.interaction.dto.user.UserDto;
import ru.practicum.ewm.interaction.enums.EventState;
import ru.practicum.ewm.interaction.enums.ParticipationRequestStatus;
import ru.practicum.ewm.interaction.error.exception.BadRequestException;
import ru.practicum.ewm.interaction.error.exception.ConflictException;
import ru.practicum.ewm.interaction.error.exception.NotFoundException;
import ru.practicum.ewm.stats.client.analyzer.AnalyzerClient;
import ru.practicum.ewm.stats.client.collector.CollectorClient;
import ru.practicum.ewm.stats.messages.RecommendedEventProto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final EventQdslDao eventQdslDao;
    private final CategoryDao categoryDao;
    private final UserClient userClient;

    private final CollectorClient collectorClient;
    private final AnalyzerClient analyzerClient;
    private final RequestClient requestClient;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventRequest request) {
        log.info("Создание события пользователем с ID: {}", userId);

        if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем за 2 часа от текущего момента");
        }

        Category category = categoryDao.findById(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));

        UserDto userDto = userClient.getUser(userId);

        Event event = EventMapper.mapToEvent(request, category.getId(), userDto.getId());
        Event savedEvent = eventDao.save(event);

        log.info("Создано событие с ID: {}", savedEvent.getId());

        return this.mapToFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Получение событий пользователя с ID: {}, from={}, size={}", userId, from, size);

        UserDto userDto = userClient.getUser(userId);
        if (userDto == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventDao.findAllByUserId(userId, pageable);

        return this.mapToShortDtos(events);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Получение события с ID: {} пользователя с ID: {}", eventId, userId);

        Event event = eventDao.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с ID=%d пользователя с ID=%d не найдено", eventId, userId)));

        return this.mapToFullDto(event);
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        return eventDao.findById(eventId).map(this::mapToFullDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.info("Обновление события с ID: {} пользователем с ID: {}", eventId, userId);

        Event event = eventDao.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с ID=%d пользователя с ID=%d не найдено", eventId, userId)));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Нельзя изменить опубликованное событие");
        }

        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем за 2 часа от текущего момента");
        }

        if (request.getCategory() != null) {
            categoryDao.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));
        }

        EventMapper.updateEventFromUserRequest(event, request);

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        Event updatedEvent = eventDao.save(event);
        log.info("Событие с ID: {} обновлено пользователем", eventId);

        return this.mapToFullDto(updatedEvent);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Integer from, Integer size) {
        log.info("Получение событий администратором с параметрами: users={}, states={}, categories={}",
                users, states, categories);


        List<EventState> stateEnums = null;
        if (states != null) {
            stateEnums = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventQdslDao
                .findAllByParams(users, stateEnums, categories, rangeStart, rangeEnd, pageable);

        return this.mapToFullDtos(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        log.info("Обновление события с ID: {} администратором", eventId);

        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + eventId + " не найдено"));

        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Дата события должна быть не ранее чем за 1 час от текущего момента");
        }

        if (request.getStateAction() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException(
                        String.format("Событие должно быть в состоянии PENDING. Текущее состояние: %s",
                                event.getState()));
            }

            switch (request.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (request.getCategory() != null) {
            categoryDao.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));
        }

        EventMapper.updateEventFromAdminRequest(event, request);

        Event updatedEvent = eventDao.save(event);
        log.info("Событие с ID: {} обновлено администратором; {}", eventId, event);

        return this.mapToFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, EventSorting sort,
                                               Integer from, Integer size) {
        log.info("Получение публичных событий с параметрами: text={}, categories={}, paid={}",
                text, categories, paid);

        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("End date is before start date");
        }

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }

        Pageable pageable;
        if (sort != null && sort.equals(EventSorting.EVENT_DATE)) {
            Sort sortByEventDate = Sort.by(Sort.Direction.DESC, "eventDate");
            pageable = PageRequest.of(from / size, size, sortByEventDate);
        } else {
            pageable = PageRequest.of(from / size, size);
        }


        List<Event> events = eventQdslDao.findAllPublicByParams(text, categories, paid, rangeStart, rangeEnd, pageable);
        List<EventShortDto> eventShortDtos = this.mapToShortDtos(events);
        if (sort != null && sort.equals(EventSorting.VIEWS)) {
            return eventShortDtos.stream().sorted(Comparator.comparing(EventShortDto::getRating).reversed()).toList();
        }

        return eventShortDtos;
    }

    @Override
    public EventFullDto getPublicEvent(Long userId, Long id) {
        log.info("Получение публичного события с ID: {}", id);

        Event event = eventDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + id + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }

        collectorClient.sendView(userId, id);

        return this.mapToFullDto(event);
    }

    @Override
    public List<EventShortDto> mapToShortDtos(List<Event> events) {
        EventMapper mapper = initMapperForEvents(events);
        List<EventShortDto> dtos = events.stream().map(mapper::mapToShortDto).toList();

        populateShortWithRating(dtos);
        return dtos;
    }

    @Override
    public void likeEvent(Long userId, Long eventId) {
        try {
            ParticipationRequestDto request = requestClient.getEventParticipationRequest(userId, eventId);
            if (!ParticipationRequestStatus.CONFIRMED.name().equals(request.getStatus())) {
                throw new BadRequestException("User can only like attended events.");
            }

            collectorClient.sendLike(userId, eventId);
        } catch (FeignException e) {
            throw new BadRequestException("Error calling request-service.");
        }
    }

    @Override
    public List<EventFullDto> getRecommendations(Long userId, Integer maxResults) {
        List<Long> ids = analyzerClient.getRecommendations(userId, maxResults)
                .stream()
                .map(RecommendedEventProto::getEventId)
                .toList();

        List<Event> events = eventDao.findAllByIdIn(ids);

        List<EventFullDto> dtos = new ArrayList<>(this.mapToFullDtos(events));

        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            order.put(ids.get(i), i);
        }

        dtos.sort(Comparator.comparingInt(dto -> order.getOrDefault(dto.getId(), Integer.MAX_VALUE)));

        return dtos;
    }

    private EventFullDto mapToFullDto(Event event) {
        EventMapper mapper = initMapperForEvents(List.of(event));
        EventFullDto dto = mapper.mapToFullDto(event);

        populateFullWithRating(List.of(dto));
        return dto;
    }

    private List<EventFullDto> mapToFullDtos(List<Event> events) {
        EventMapper mapper = initMapperForEvents(events);
        List<EventFullDto> dtos = events.stream()
                .map(mapper::mapToFullDto)
                .toList();

        populateFullWithRating(dtos);
        return dtos;
    }

    private EventMapper initMapperForEvents(List<Event> events) {
        return new EventMapper(
                getUserDtoForEvents(events),
                getCategoriesDtoForEvents(events));
    }

    private Map<Long, UserDto> getUserDtoForEvents(List<Event> events) {
        Set<Long> initiatorIds = events.stream().map(Event::getUserId).collect(Collectors.toSet());
        if (initiatorIds.isEmpty()) {
            return Map.of();
        }

        return userClient.getUsers(initiatorIds, 0, initiatorIds.size())
                .stream()
                .collect(Collectors.toMap(UserDto::getId, u -> u));
    }

    private Map<Long, CategoryDto> getCategoriesDtoForEvents(List<Event> events) {
        List<Long> categoryIds = events.stream().map(Event::getCategoryId).toList();
        if (categoryIds.isEmpty()) {
            return Map.of();
        }

        return categoryDao.findAllByIdIn(categoryIds)
                .stream()
                .map(CategoryMapper::mapToDto)
                .collect(Collectors.toMap(CategoryDto::getId, c -> c));
    }

    private void populateFullWithRating(Collection<EventFullDto> dtos) {
        List<Long> eventIds = dtos.stream().map(EventFullDto::getId).toList();
        Map<Long, Double> interactions = analyzerClient.getInteractionsCount(eventIds);
        for (EventFullDto dto : dtos) {
            dto.setRating(interactions.get(dto.getId()));
        }
    }

    private void populateShortWithRating(Collection<EventShortDto> dtos) {
        List<Long> eventIds = dtos.stream().map(EventShortDto::getId).toList();
        Map<Long, Double> interactions = analyzerClient.getInteractionsCount(eventIds);
        for (EventShortDto dto : dtos) {
            dto.setRating(interactions.get(dto.getId()));
        }
    }

}