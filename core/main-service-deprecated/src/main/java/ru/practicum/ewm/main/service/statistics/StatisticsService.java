package ru.practicum.ewm.main.service.statistics;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.EventShortDto;
import ru.practicum.ewm.stat.client.StatClient;
import ru.practicum.ewm.stat.dto.StatEventCreateDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class StatisticsService {
    private final StatClient client;

    public void saveHit(HttpServletRequest request) {
        StatEventCreateDto dto = new StatEventCreateDto();
        dto.setApp("main-service");
        dto.setIp(request.getRemoteAddr());
        dto.setUri(request.getRequestURI());
        dto.setTimestamp(LocalDateTime.now());

        log.info("Saving hit info: app: {}; ip: {}; uri: {}; timestamp: {}",
                dto.getApp(), dto.getIp(), dto.getUri(), dto.getTimestamp());

        client.saveHit(dto);
    }

    public void populateWithViews(List<EventFullDto> dtos) {
        LocalDateTime start = dtos.stream()
                .map(EventFullDto::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (start == null) {
            return;
        }

        LocalDateTime end = LocalDateTime.now();

        Map<Long, Integer> viewsById = this.getEventsViews(
                start,
                end,
                dtos.stream().map(EventFullDto::getId).toList());

        dtos.forEach(dto -> dto.setViews(viewsById.getOrDefault(dto.getId(), 0)));
    }

    public void populateWithViews(EventFullDto dto) {
        LocalDateTime start = dto.getPublishedOn();
        if (start == null) {
            return;
        }

        Map<Long, Integer> result = getEventsViews(
                dto.getPublishedOn(),
                LocalDateTime.now(),
                List.of(dto.getId()));
        dto.setViews(result.getOrDefault(dto.getId(), 0));
    }

    public void populateWithViews(LocalDateTime start, List<EventShortDto> dtos) {
        LocalDateTime end = LocalDateTime.now();
        if (start == null) {
            return;
        }

        Map<Long, Integer> viewsById = this.getEventsViews(
                start,
                end,
                dtos.stream().map(EventShortDto::getId).toList());

        dtos.forEach(dto -> dto.setViews(viewsById.getOrDefault(dto.getId(), 0)));
    }

    private Map<Long, Integer> getEventsViews(LocalDateTime start, LocalDateTime end, List<Long> eventIds) {
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .toList();

        Map<Long, Integer> viewsMap = new HashMap<>();
        client.getStats(start, end, uris, true).forEach(s -> {
            long eventId = Long.parseLong(s.getUri().split("/")[2]);
            viewsMap.put(eventId, s.getHits().intValue());
        });

        return viewsMap;
    }
}
