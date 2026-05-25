package ru.practicum.ewm.event.repository.event;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.interaction.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventQdslDao {
    List<Event> findAllByParams(List<Long> users,
                                List<EventState> states,
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
