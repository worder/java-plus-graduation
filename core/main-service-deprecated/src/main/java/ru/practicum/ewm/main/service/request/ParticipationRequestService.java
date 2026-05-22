package ru.practicum.ewm.main.service.request;

import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.dto.request.ParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.main.dto.request.ParticipationRequestStatusUpdateResultDto;

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
}
