package ru.practicum.ewm.request.service;

import ru.practicum.ewm.interaction.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestStatusUpdateResultDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto createEventParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelEventParticipationRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserParticipationRequests(Long userId);

    List<ParticipationRequestDto> getParticipationRequestsForUserEvent(Long userId, Long eventId);

    ParticipationRequestStatusUpdateResultDto updateParticipationRequestsStatus(
            Long userId,
            Long eventId,
            ParticipationRequestStatusUpdateRequest request);

    ParticipationRequestDto getRequesterParticipationRequest(Long requesterId, Long requestId);
}
