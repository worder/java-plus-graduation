package ru.practicum.ewm.stats.analyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.analyzer.kafka.KafkaClient;
import ru.practicum.ewm.stats.analyzer.service.AnalyzerService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Initializes and manages the lifecycle of Kafka consumers.
 * It implements CommandLineRunner to start processing after the Spring context initializes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerKafkaStarter implements CommandLineRunner {
    private final KafkaClient kafkaClient;
    private final AnalyzerService analyzerService;

    // Timeout for the poll call (how long we wait for records)
    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(5);
    // Delay between polling cycles when no records are found (to avoid high CPU load)
    private static final Duration CONSUME_INTERVAL = Duration.ofSeconds(1);

    @Value("${app.kafka.topic.user-actions}")
    private String topicUserActions;

    @Value("${app.kafka.topic.events-similarity}")
    private String topicEventsSimilarity;

    @Override
    public void run(String... args) {
        log.info("Starting Kafka Consumer Listeners...");

        // 1. Start UserAction processing in a separate thread
        Thread userActionThread = new Thread(this::processUserActions, "user-action-consumer");
        userActionThread.setDaemon(true); // Ensures the thread does not block application shutdown
        userActionThread.start();

        // 2. Start EventSimilarity processing in a separate thread
        Thread eventSimilarityThread = new Thread(this::processEventSimilarities, "event-similarity-consumer");
        eventSimilarityThread.setDaemon(true);
        eventSimilarityThread.start();

        log.info("Kafka Consumers started successfully on separate threads.");
    }

    /**
     * Main processing loop for UserAction records.
     */
    private void processUserActions() {
        try (Consumer<Long, UserActionAvro> consumer = kafkaClient.getKafkaUserActionConsumer()) {

            consumer.subscribe(List.of(topicUserActions));

            log.info("Starting poll loop for UserAction topic.");
            // Infinite loop to continuously consume records
            while (!Thread.currentThread().isInterrupted()) {
                // Poll for records, waiting up to POLL_TIMEOUT seconds
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(POLL_TIMEOUT);

                if (records.isEmpty()) {
                    // If no records are available, sleep for the defined CONSUME_INTERVAL
                    try {
                        TimeUnit.MILLISECONDS.sleep(CONSUME_INTERVAL.toMillis());
                    } catch (InterruptedException e) {
                        log.warn("UserAction consumer sleep interrupted. Shutting down.");
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue; // Skip processing and continue the loop
                }

                log.info("Received {} UserAction records.", records.count());

                // Process the batch of records
                records.forEach(record -> {
                    try {
                        UserActionAvro action = record.value();
                        // Call the business logic service
                        analyzerService.analyzeUserAction(action);
                        log.debug("Processed UserAction record key: {}", record.key());
                    } catch (Exception e) {
                        log.error("Error processing UserAction record (key: {}). Skipping record.", record.key(), e);
                    }
                });

                // Commit offsets only after the entire batch is successfully processed
                consumer.commitSync();
                log.info("Successfully committed offset for UserAction batch.");
            }
        } catch (Exception e) {
            log.error("Fatal error in UserAction consumer loop. Stopping consumer.", e);
        } finally {
            log.warn("UserAction Consumer thread shutting down and closing client.");
        }
    }

    /**
     * Main processing loop for EventSimilarity records.
     */
    private void processEventSimilarities() {
        try (Consumer<Long, EventSimilarityAvro> consumer = kafkaClient.getKafkaEventSimilarityConsumer()) {

            consumer.subscribe(List.of(topicEventsSimilarity));

            log.info("Starting poll loop for EventSimilarity topic.");
            // Infinite loop to continuously consume records
            while (!Thread.currentThread().isInterrupted()) {
                // Poll for records, waiting up to POLL_TIMEOUT seconds
                ConsumerRecords<Long, EventSimilarityAvro> records = consumer.poll(POLL_TIMEOUT);

                if (records.isEmpty()) {
                    // If no records are available, sleep for the defined CONSUME_INTERVAL
                    try {
                        TimeUnit.MILLISECONDS.sleep(CONSUME_INTERVAL.toMillis());
                    } catch (InterruptedException e) {
                        log.warn("EventSimilarity consumer sleep interrupted. Shutting down.");
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue; // Skip processing and continue the loop
                }

                log.info("Received {} EventSimilarity records.", records.count());

                // Process the batch of records
                records.forEach(record -> {
                    try {
                        EventSimilarityAvro similarity = record.value();
                        // Call the business logic service
                        analyzerService.analyzeEventSimilarity(similarity);
                        log.debug("Processed EventSimilarity record key: {}", record.key());
                    } catch (Exception e) {
                        log.error("Error processing EventSimilarity record (key: {}). Skipping record.", record.key(), e);
                    }
                });

                // Commit offsets only after the entire batch is successfully processed
                consumer.commitSync();
                log.info("Successfully committed offset for EventSimilarity batch.");
            }
        } catch (Exception e) {
            log.error("Fatal error in EventSimilarity consumer loop. Stopping consumer.", e);
        } finally {
            // Clean up resources when the thread exits
            log.warn("EventSimilarity Consumer thread shutting down and closing client.");
        }
    }
}
