package ru.practicum.ewm.stat.service;

import ru.practicum.ewm.stat.dto.StatEventCreateDto;
import ru.practicum.ewm.stat.dto.StatEventViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void createStatEvent(StatEventCreateDto eventRequest);

    List<StatEventViewDto> findStatEvents(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique);
}
