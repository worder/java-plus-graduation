package ru.practicum.ewm.event.mapper;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.interaction.dto.category.CategoryDto;
import ru.practicum.ewm.interaction.dto.event.*;
import ru.practicum.ewm.interaction.dto.user.UserDto;
import ru.practicum.ewm.interaction.dto.user.UserShortDto;
import ru.practicum.ewm.interaction.enums.EventState;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class EventMapper {
    private final Map<Long, UserDto> prefetchedUsers;
    private final Map<Long, CategoryDto> prefetchedCategories;

    public EventFullDto mapToFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .description(event.getDescription())
                .state(event.getState())
                .initiator(prefetchedUsers.get(event.getUserId()))
                .category(prefetchedCategories.get(event.getCategoryId()))
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

    public EventShortDto mapToShortDto(Event event) {
        UserDto user = prefetchedUsers.get(event.getUserId());
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .initiator(UserShortDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build())
                .category(prefetchedCategories.get(event.getCategoryId()))
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static Event mapToEvent(NewEventRequest request, Long categoryId, Long userId) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setAnnotation(request.getAnnotation());
        event.setDescription(request.getDescription());
        event.setCategoryId(categoryId);
        event.setUserId(userId);
        event.setEventDate(request.getEventDate());
        event.setLocationLat(request.getLocation().getLat());
        event.setLocationLon(request.getLocation().getLon());
        event.setPaid(request.getPaid() != null ? request.getPaid() : false);
        event.setParticipantLimit(request.getParticipantLimit() != null ? request.getParticipantLimit() : 0);
        event.setRequestModeration(request.getRequestModeration() != null ? request.getRequestModeration() : true);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        return event;
    }

    public static void updateEventFromUserRequest(Event event, UpdateEventUserRequest request) {
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getCategory() != null) event.setCategoryId(request.getCategory());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getLocation() != null) {
            event.setLocationLat(request.getLocation().getLat());
            event.setLocationLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) event.setPaid(request.getPaid());
        if (request.getParticipantLimit() != null) event.setParticipantLimit(request.getParticipantLimit());
        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());
    }

    public static void updateEventFromAdminRequest(Event event, UpdateEventAdminRequest request) {
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getCategory() != null) event.setCategoryId(request.getCategory());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getLocation() != null) {
            event.setLocationLat(request.getLocation().getLat());
            event.setLocationLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) event.setPaid(request.getPaid());
        if (request.getParticipantLimit() != null) event.setParticipantLimit(request.getParticipantLimit());
        if (request.getConfirmedRequests() != null) event.setConfirmedRequests(request.getConfirmedRequests());
        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());
    }
}