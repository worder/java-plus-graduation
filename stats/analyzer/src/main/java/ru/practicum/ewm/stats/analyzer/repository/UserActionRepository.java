package ru.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.analyzer.model.UserAction;
import ru.practicum.ewm.stats.analyzer.repository.projection.EventWeightSumProjection;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    Boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<UserAction> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
            SELECT
                ua.eventId AS eventId,
                COALESCE(SUM(ua.weight), 0) AS totalWeight
            FROM UserAction ua
            WHERE ua.eventId IN :eventIds
            GROUP BY ua.eventId
            """)
    List<EventWeightSumProjection> sumWeightsByEventIds(Collection<Long> eventIds);

    List<UserAction> findAllByUserId(Long userId, PageRequest pageRequest);

    List<UserAction> findAllByUserId(Long userId);
}