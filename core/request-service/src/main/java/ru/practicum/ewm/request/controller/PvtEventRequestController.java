package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestStatusUpdateResultDto;
import ru.practicum.ewm.request.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
@Validated
public class PvtEventRequestController {
    private final ParticipationRequestService requestService;

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipationRequests(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        return requestService.getParticipationRequestsForUserEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/request")
    public ParticipationRequestDto getEventParticipationRequest(@PathVariable Long userId,
                                                                @PathVariable Long eventId) {
        return requestService.getRequesterParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public ParticipationRequestStatusUpdateResultDto updateParticipationRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody ParticipationRequestStatusUpdateRequest request) {
        return requestService.updateParticipationRequestsStatus(userId, eventId, request);
    }
}