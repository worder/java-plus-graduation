package ru.practicum.ewm.main.dto.event;

import ru.practicum.ewm.main.dto.category.CategoryMapper;
import ru.practicum.ewm.main.dto.user.UserMapper;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;

public class EventMapper {
    public static EventFullDto mapToFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .description(event.getDescription())
                .state(event.getState())
                .initiator(UserMapper.mapToDto(event.getUser()))
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .location(EventLocationDto.builder()
                        .lat(event.getLocationLat())
                        .lon(event.getLocationLon())
                        .build())
                .confirmedRequests(event.getConfirmedRequests())
                .participantLimit(event.getParticipantLimit())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .paid(event.getPaid())
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static EventShortDto mapToShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .initiator(UserMapper.mapToShortDto(event.getUser()))
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static Event mapToEvent(NewEventRequest request, Category category, User user) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setAnnotation(request.getAnnotation());
        event.setDescription(request.getDescription());
        event.setCategory(category);
        event.setUser(user);
        event.setEventDate(request.getEventDate());
        event.setLocationLat(request.getLocation().getLat());
        event.setLocationLon(request.getLocation().getLon());
        event.setPaid(request.getPaid() != null ? request.getPaid() : false);
        event.setParticipantLimit(request.getParticipantLimit() != null ? request.getParticipantLimit() : 0);
        event.setRequestModeration(request.getRequestModeration() != null ? request.getRequestModeration() : true);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(Event.EventState.PENDING);
        return event;
    }

    public static void updateEventFromUserRequest(Event event, UpdateEventUserRequest request, Category category) {
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (category != null) event.setCategory(category);
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getLocation() != null) {
            event.setLocationLat(request.getLocation().getLat());
            event.setLocationLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) event.setPaid(request.getPaid());
        if (request.getParticipantLimit() != null) event.setParticipantLimit(request.getParticipantLimit());
        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());
    }

    public static void updateEventFromAdminRequest(Event event, UpdateEventAdminRequest request, Category category) {
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (category != null) event.setCategory(category);
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getLocation() != null) {
            event.setLocationLat(request.getLocation().getLat());
            event.setLocationLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) event.setPaid(request.getPaid());
        if (request.getParticipantLimit() != null) event.setParticipantLimit(request.getParticipantLimit());
        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());
    }
}