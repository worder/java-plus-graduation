package ru.practicum.ewm.main.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.EventShortDto;
import ru.practicum.ewm.main.service.event.EventService;
import ru.practicum.ewm.main.service.statistics.StatisticsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    private final StatisticsService statisticsService;

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "EVENT_DATE") EventService.EventSorting sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest httpRequest) {
        statisticsService.saveHit(httpRequest);
        return eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id,
                                 HttpServletRequest httpRequest) {
        statisticsService.saveHit(httpRequest);
        return eventService.getPublicEvent(id);
    }
}