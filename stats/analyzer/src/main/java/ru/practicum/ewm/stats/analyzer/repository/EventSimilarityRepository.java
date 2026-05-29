package ru.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    Boolean existsByEventAAndEventB(Long eventA, Long eventB);

    Optional<EventSimilarity> findByEventAAndEventB(Long eventA, Long eventB);

    List<EventSimilarity> findAllByEventAInOrEventBIn(Set<Long> eventAIds, Set<Long> eventBIds, PageRequest pageRequest);

    @Query("SELECT e FROM EventSimilarity e WHERE e.eventA = :eventId OR e.eventB = :eventId")
    List<EventSimilarity> findAllByEventId(Long eventId, Pageable pageable);
}
