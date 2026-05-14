package ru.practicum.ewm.main.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dao.category.CategoryDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dao.event.EventQdslDao;
import ru.practicum.ewm.main.dao.user.UserDao;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.error.exception.BadRequestException;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.service.statistics.StatisticsService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final EventQdslDao eventQdslDao;
    private final CategoryDao categoryDao;
    private final UserDao userDao;
    private final StatisticsService statisticsService;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventRequest request) {
        log.info("Создание события пользователем с ID: {}", userId);

        if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем за 2 часа от текущего момента");
        }

        Category category = categoryDao.findById(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

        Event event = EventMapper.mapToEvent(request, category, user);
        Event savedEvent = eventDao.save(event);

        log.info("Создано событие с ID: {}", savedEvent.getId());

        return this.mapToFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Получение событий пользователя с ID: {}, from={}, size={}", userId, from, size);

        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

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
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.info("Обновление события с ID: {} пользователем с ID: {}", eventId, userId);

        Event event = eventDao.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с ID=%d пользователя с ID=%d не найдено", eventId, userId)));

        if (event.getState() == Event.EventState.PUBLISHED) {
            throw new ConflictException("Нельзя изменить опубликованное событие");
        }

        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем за 2 часа от текущего момента");
        }

        Category category = null;
        if (request.getCategory() != null) {
            category = categoryDao.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));
        }

        EventMapper.updateEventFromUserRequest(event, request, category);

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(Event.EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(Event.EventState.CANCELED);
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


        List<Event.EventState> stateEnums = null;
        if (states != null) {
            stateEnums = states.stream()
                    .map(Event.EventState::valueOf)
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
            if (event.getState() != Event.EventState.PENDING) {
                throw new ConflictException(
                        String.format("Событие должно быть в состоянии PENDING. Текущее состояние: %s",
                                event.getState()));
            }

            switch (request.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(Event.EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(Event.EventState.CANCELED);
                    break;
            }
        }

        Category category = null;
        if (request.getCategory() != null) {
            category = categoryDao.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));
        }

        EventMapper.updateEventFromAdminRequest(event, request, category);

        Event updatedEvent = eventDao.save(event);
        log.info("Событие с ID: {} обновлено администратором", eventId);

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
            return eventShortDtos.stream().sorted(Comparator.comparing(EventShortDto::getViews).reversed()).toList();
        }

        return eventShortDtos;
    }

    @Override
    public EventFullDto getPublicEvent(Long id) {
        log.info("Получение публичного события с ID: {}", id);

        Event event = eventDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + id + " не найдено"));

        if (event.getState() != Event.EventState.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }

        return this.mapToFullDto(event);
    }

    @Override
    public List<EventShortDto> mapToShortDtos(List<Event> events) {
        List<EventShortDto> dtos = events.stream().map(EventMapper::mapToShortDto).toList();

        LocalDateTime start = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        statisticsService.populateWithViews(start, dtos);
        return dtos;
    }

    private EventFullDto mapToFullDto(Event event) {
        EventFullDto dto = EventMapper.mapToFullDto(event);
        statisticsService.populateWithViews(dto);
        return dto;
    }

    private List<EventFullDto> mapToFullDtos(List<Event> events) {
        List<EventFullDto> dtos = events.stream()
                .map(EventMapper::mapToFullDto)
                .toList();

        statisticsService.populateWithViews(dtos);
        return dtos;
    }
}