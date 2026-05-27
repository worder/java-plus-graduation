package ru.practicum.ewm.stats.aggregator.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.serialization.deserializer.UserActionAvroDeserializer;
import ru.practicum.ewm.stats.serialization.serializer.GeneralAvroSerializer;

import java.time.Duration;
import java.util.Properties;

@Component
public class KafkaClientImpl implements KafkaClient {
    private Producer<Long, SpecificRecordBase> producer;
    private Consumer<Long, SpecificRecordBase> consumer;

    @Value("${app.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.consumer.group-id:aggregator}")
    private String consumerGroupId;

    public Producer<Long, SpecificRecordBase> getProducer() {
        if (producer == null) {
            Properties config = new Properties();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getCanonicalName());
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

            this.producer = new KafkaProducer<>(config);
        }

        return producer;
    }

    public Consumer<Long, SpecificRecordBase> getConsumer() {
        if (consumer == null) {
            Properties config = new Properties();
            config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
            config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getCanonicalName());
            config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionAvroDeserializer.class);
            config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

            this.consumer = new KafkaConsumer<>(config);

            Runtime.getRuntime().addShutdownHook(new Thread(this.consumer::wakeup));
        }

        return consumer;
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close(Duration.ofSeconds(5));
        }
    }
}
