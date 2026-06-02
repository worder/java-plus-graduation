package ru.practicum.ewm.stats.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.aggregator.kafka.KafkaClient;
import ru.practicum.ewm.stats.aggregator.service.AggregatorService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final AggregatorService aggregatorService;
    private final KafkaClient kafkaClient;

    @Value("${app.kafka.topic.user-actions}")
    private String userActionsTopic;

    @Value("${app.kafka.topic.events-similarity}")
    private String eventsSimilarityTopic;

    public void start() {
        Consumer<Long, SpecificRecordBase> consumer = kafkaClient.getConsumer();

        try {
            consumer.subscribe(List.of(userActionsTopic));

            while (true) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(5));
                if (records.isEmpty()) {
                    continue;
                }

                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    List<EventSimilarityAvro> eventsSimilarity = aggregatorService.aggregateUserAction(record.value());
                    if (!eventsSimilarity.isEmpty()) {
                        sendInProducer(eventsSimilarity);
                    }
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignore) {
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Closing consumer");
                consumer.close();
            }
        }
    }

    private void sendInProducer(List<EventSimilarityAvro> eventsSimilarity) {
        for (EventSimilarityAvro eventSimilarity : eventsSimilarity) {
            kafkaClient.getProducer().send(new ProducerRecord<>(
                    eventsSimilarityTopic,
                    null,
                    eventSimilarity.getTimestamp().toEpochMilli(),
                    eventSimilarity.getEventA(),
                    eventSimilarity
            ));
        }
    }
}
