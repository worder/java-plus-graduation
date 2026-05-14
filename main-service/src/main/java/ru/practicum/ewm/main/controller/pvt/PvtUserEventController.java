package ru.practicum.ewm.main.controller.pvt;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.EventShortDto;
import ru.practicum.ewm.main.dto.event.NewEventRequest;
import ru.practicum.ewm.main.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.dto.request.ParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.main.dto.request.ParticipationRequestStatusUpdateResultDto;
import ru.practicum.ewm.main.service.event.EventService;
import ru.practicum.ewm.main.service.request.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
public class PvtUserEventController {
    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventRequest request) {
        return eventService.createEvent(userId, request);
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest request) {
        return eventService.updateEventByUser(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipationRequests(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        return requestService.getParticipationRequestsForUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public ParticipationRequestStatusUpdateResultDto updateParticipationRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody ParticipationRequestStatusUpdateRequest request) {
        return requestService.updateParticipationRequestsStatus(userId, eventId, request);
    }
}