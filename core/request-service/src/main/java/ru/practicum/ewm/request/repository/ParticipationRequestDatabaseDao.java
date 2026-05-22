package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.ParticipationRequest;

public interface ParticipationRequestDatabaseDao extends
        ParticipationRequestDao,
        JpaRepository<ParticipationRequest, Long> {
}
