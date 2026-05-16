package ru.practicum.ewm.stat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.StatEventCreateDto;
import ru.practicum.ewm.stat.dto.StatEventViewDto;
import ru.practicum.ewm.stat.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class StatsController {
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHitEvent(@RequestBody StatEventCreateDto hitEventRequest) {
        log.info("POST /hit: {}", hitEventRequest);
        this.statService.createStatEvent(hitEventRequest);
    }

    @GetMapping("/stats")
    public List<StatEventViewDto> getStatEvents(
            @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        log.info("GET /stats, start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        return this.statService.findStatEvents(start, end, uris, unique);
    }
}
