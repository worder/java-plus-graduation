package ru.practicum.ewm.stats.collector.kafka;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.serialization.serializer.GeneralAvroSerializer;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

@Component
public class KafkaClient {
    private Producer<Long, SpecificRecordBase> producer;

    @Value("${app.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    protected Producer<Long, SpecificRecordBase> getProducer() {
        if (producer == null) {
            Properties config = new Properties();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                    org.apache.kafka.common.serialization.LongSerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

            producer = new KafkaProducer<>(config);
        }

        return producer;
    }

    public void send(String topic, Instant timestamp, Long recordId, SpecificRecordBase record) {
        this.getProducer().send(
                new ProducerRecord<>(topic, null, timestamp.toEpochMilli(), recordId, record));
    }

    @PreDestroy
    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close(Duration.ofSeconds(5));
        }
    }
}
