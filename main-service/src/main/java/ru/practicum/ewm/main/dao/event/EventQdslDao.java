package ru.practicum.ewm.main.dao.event;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventQdslDao {
    List<Event> findAllByParams(List<Long> users,
                                List<Event.EventState> states,
                                List<Long> categories,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                Pageable pageable);

    List<Event> findAllPublicByParams(String text,
                                      List<Long> categories,
                                      Boolean paid,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Pageable pageable);
}
