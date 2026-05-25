package ru.practicum.ewm.event.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.service.event.EventService;
import ru.practicum.ewm.interaction.dto.event.EventFullDto;
import ru.practicum.ewm.interaction.dto.event.EventShortDto;
import ru.practicum.ewm.interaction.dto.event.NewEventRequest;
import ru.practicum.ewm.interaction.dto.event.UpdateEventUserRequest;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
@Validated
public class PvtUserEventController {
    private final EventService eventService;

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
}