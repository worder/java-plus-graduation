package ru.practicum.ewm.stat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.stat.dto.StatEventCreateDto;
import ru.practicum.ewm.stat.dto.StatEventViewDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatServiceImplTest {
    @Autowired
    StatServiceImpl statService;

    @Test
    void createStatEvent_ShouldCreateEvent() {
        LocalDateTime date = LocalDateTime.of(2025, 12, 20, 20, 0, 0);

        StatEventCreateDto hitEvent = new StatEventCreateDto();
        hitEvent.setIp("192.168.0.1");
        hitEvent.setApp("ewm-main-service");
        hitEvent.setUri("/events/1");
        hitEvent.setTimestamp(date);

        statService.createStatEvent(hitEvent);

        assertEquals(1, statService.findStatEvents(
                date.minusMinutes(1),
                date.plusMinutes(1),
                List.of(), false).size());
    }

    @Test
    void findStatEvents_ShouldReturnStatistics() {
        LocalDateTime date1 = LocalDateTime.of(2025, 12, 20, 20, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2025, 12, 20, 18, 0, 0);
        LocalDateTime date3 = LocalDateTime.of(2025, 12, 20, 19, 0, 0);

        // register hit: /events/1
        StatEventCreateDto hitEvent = new StatEventCreateDto();
        hitEvent.setIp("192.168.0.1");
        hitEvent.setApp("ewm-main-service");
        hitEvent.setUri("/events/1");
        hitEvent.setTimestamp(date1);
        statService.createStatEvent(hitEvent);

        // register another hit: /events/1
        hitEvent.setTimestamp(date2);
        statService.createStatEvent(hitEvent);

        // register hit: /events/2
        hitEvent.setUri("/events/2");
        hitEvent.setTimestamp(date3);
        statService.createStatEvent(hitEvent);

        List<StatEventViewDto> events = statService.findStatEvents(
                date1.minusDays(1),
                date1.plusDays(1),
                List.of(), false);

        assertEquals(2, events.size());
    }
}
