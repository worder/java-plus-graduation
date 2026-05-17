package ru.practicum.ewm.main.dao.request;

import ru.practicum.ewm.main.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestDao {
    ParticipationRequest save(ParticipationRequest participationRequest);

    List<ParticipationRequest> findByIdIn(List<Long> ids);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    Optional<ParticipationRequest> findById(Long id);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findByEventId(Long eventId);

    Integer countByEventIdAndStatus(Long eventId, ParticipationRequest.Status status);
}
