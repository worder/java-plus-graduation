package ru.practicum.ewm.main.dao.event;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventDao {
    Event save(Event event);

    Optional<Event> findById(Long id);

    Optional<Event> findByIdAndUserId(Long eventId, Long userId);

    List<Event> findAllByUserId(Long userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> ids);

    Integer countByCategoryId(Long categoryId);
}