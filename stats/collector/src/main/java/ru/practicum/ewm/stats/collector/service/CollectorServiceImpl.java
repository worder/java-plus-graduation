package ru.practicum.ewm.stats.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.collector.kafka.KafkaClient;
import ru.practicum.ewm.stats.collector.mapper.UserActionMapper;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.messages.UserActionProto;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final KafkaClient kafkaClient;

    @Value("${app.kafka.topic.user-actions:stats.user-actions.v1}")
    private String userActionsTopic;

    @Override
    public void collectUserAction(UserActionProto request) {
        UserActionAvro userActionAvro = UserActionMapper.toUserActionAvro(request);
        log.info("Sending user action to topic: {}, proto: [{}]; avro: [{}]", userActionsTopic, request, userActionAvro);
        kafkaClient.send(userActionsTopic, userActionAvro.getTimestamp(), userActionAvro.getEventId(), userActionAvro);
    }
}
