package ru.practicum.ewm.main.dao.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.ParticipationRequest;

public interface ParticipationRequestDatabaseDao extends
        ParticipationRequestDao,
        JpaRepository<ParticipationRequest, Long> {
}
