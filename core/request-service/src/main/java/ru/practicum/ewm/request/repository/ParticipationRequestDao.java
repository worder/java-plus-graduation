package ru.practicum.ewm.request.repository;

import ru.practicum.ewm.interaction.enums.ParticipationRequestStatus;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestDao {
    ParticipationRequest save(ParticipationRequest participationRequest);

    List<ParticipationRequest> findByIdIn(List<Long> ids);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    Optional<ParticipationRequest> findById(Long id);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findByEventId(Long eventId);

    Integer countByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);
}
