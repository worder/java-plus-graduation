package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.request.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@AllArgsConstructor
public class PvtUserRequestController {
    private final ParticipationRequestService requestService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        return requestService.createEventParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
        return requestService.cancelEventParticipationRequest(userId, requestId);
    }

    @GetMapping()
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId) {
        return requestService.getUserParticipationRequests(userId);
    }
}
