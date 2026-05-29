package ru.practicum.ewm.stats.analyzer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.serialization.deserializer.EventSimilarityAvroDeserializer;
import ru.practicum.ewm.stats.serialization.deserializer.UserActionAvroDeserializer;

import java.util.Properties;

@Slf4j
@Configuration
public class KafkaClientConfig {
    @Value("${app.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.consumer.group-id}")
    private String consumerGroupId;

    @Bean
    public Properties getKafkaConsumerUserActionProperties() {
        Properties config = new Properties();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionAvroDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return config;
    }

    @Bean
    public Properties getKafkaConsumerEventSimilarityProperties() {
        Properties config = new Properties();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityAvroDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return config;
    }

    @Bean
    public KafkaClient kafkaClient(
            @Qualifier("getKafkaConsumerUserActionProperties") Properties userActionProps,
            @Qualifier("getKafkaConsumerEventSimilarityProperties") Properties eventSimilarityProps) {
        return new KafkaClient(userActionProps, eventSimilarityProps);
    }
}