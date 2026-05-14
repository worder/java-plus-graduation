package ru.practicum.ewm.stat.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stat.dto.StatEventCreateDto;
import ru.practicum.ewm.stat.dto.StatEventViewDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatClient extends BaseClient {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatClient(@Value("${stat-server.url:http://localhost:9090}") String serverUrl) {
        super(buildRestTemplate(serverUrl));
    }

    private static RestTemplate buildRestTemplate(String serverUrl) {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);

        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(serverUrl));

        return restTemplate;
    }

    public void saveHit(StatEventCreateDto statEventCreateDto) {
        log.debug("Отправка hit на сервер статистики: {}", statEventCreateDto);

        try {
            ResponseEntity<Object> response = post("/hit", statEventCreateDto);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Не удалось сохранить hit, статус: {}", response.getStatusCode());
            } else {
                log.debug("Hit успешно сохранен");
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке hit: {}", e.getMessage(), e);
        }
    }

    public List<StatEventViewDto> getStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris,
                                           Boolean unique) {
        log.debug("Получение статистики: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        Map<String, Object> parameters;
        String path;

        if (uris != null && !uris.isEmpty()) {
            parameters = Map.of(
                    "start", start.format(DATE_TIME_FORMATTER),
                    "end", end.format(DATE_TIME_FORMATTER),
                    "uris", String.join(",", uris),
                    "unique", unique != null ? unique : false
            );
            path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        } else {
            parameters = Map.of(
                    "start", start.format(DATE_TIME_FORMATTER),
                    "end", end.format(DATE_TIME_FORMATTER),
                    "unique", unique != null ? unique : false
            );
            path = "/stats?start={start}&end={end}&unique={unique}";
        }

        try {
            ResponseEntity<Object> response = get(path, parameters);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                List<StatEventViewDto> result = mapper.convertValue(
                        response.getBody(),
                        new TypeReference<List<StatEventViewDto>>() {}
                );
                log.debug("Получено {} записей статистики", result.size());
                return result;
            } else {
                log.warn("Не удалось получить статистику, статус: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Ошибка при получении статистики: {}", e.getMessage(), e);
        }

        return List.of();
    }

    public List<StatEventViewDto> getStats(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, null, false);
    }

    public List<StatEventViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStats(start, end, uris, false);
    }

    public List<StatEventViewDto> getUniqueStats(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, null, true);
    }
}