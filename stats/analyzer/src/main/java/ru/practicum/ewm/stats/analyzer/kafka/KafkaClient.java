package ru.practicum.ewm.stats.analyzer.kafka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Getter
@Slf4j
public class KafkaClient implements AutoCloseable {
    private final Consumer<Long, UserActionAvro> kafkaUserActionConsumer;
    private final Consumer<Long, EventSimilarityAvro> kafkaEventSimilarityConsumer;

    public KafkaClient(Properties userActionProps, Properties eventSimilarityProps) {
        this.kafkaUserActionConsumer = new KafkaConsumer<>(userActionProps);
        this.kafkaEventSimilarityConsumer = new KafkaConsumer<>(eventSimilarityProps);
    }

    @Override
    public void close() {
        try {
            this.kafkaUserActionConsumer.commitSync();
            this.kafkaEventSimilarityConsumer.commitSync();
        } catch (Exception e) {
            log.error("Error during Kafka Consumer commit:", e);
        } finally {
            log.info("KafkaClient: Closing UserActionConsumer.");
            this.kafkaUserActionConsumer.close();
            log.info("KafkaClient: Closing EventSimilarityConsumer.");
            this.kafkaEventSimilarityConsumer.close();
        }
    }

}