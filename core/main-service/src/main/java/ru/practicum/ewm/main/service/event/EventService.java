package ru.practicum.ewm.main.service.event;

import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    enum EventSorting {
        EVENT_DATE,
        VIEWS
    }

    EventFullDto createEvent(Long userId, NewEventRequest request);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, EventSorting sort,
                                        Integer from, Integer size);

    EventFullDto getPublicEvent(Long id);

    List<EventShortDto> mapToShortDtos(List<Event> events);
}