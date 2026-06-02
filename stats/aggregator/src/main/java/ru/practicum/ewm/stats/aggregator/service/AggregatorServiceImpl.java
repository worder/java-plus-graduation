package ru.practicum.ewm.stats.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AggregatorServiceImpl implements AggregatorService {
    @Value("${app.action-weight.view:0.4}")
    private double viewWeight;

    @Value("${app.action-weight.register:0.8}")
    private double registerWeight;

    @Value("${app.action-weight.like:1.0}")
    private double likeWeight;

    private final Map<Long, Map<Long, Double>> eventUserWeights = new HashMap<>();
    private final Map<Long, Double> eventWeightSums = new HashMap<>();
    private final Map<Long, Map<Long, Double>> eventMinSums = new HashMap<>();

    @Override
    public List<EventSimilarityAvro> aggregateUserAction(SpecificRecordBase record) {
        UserActionAvro action = (UserActionAvro) record;

        double weightDiff = calculateWeightDiff(action);
        if (weightDiff == 0.0) {
            log.info("User action weight has not changed");
            return List.of();
        }

        updateUserWeight(action);
        updateEventWeightSum(action, weightDiff);

        List<Long> relatedEventIds = findRelatedEvents(action);

        if (relatedEventIds.isEmpty()) {
            log.info("No related events found for similarity calculation");
            return List.of();
        }

        updateEventMinSums(action, weightDiff, relatedEventIds);

        return buildSimilarities(action, relatedEventIds);
    }

    private List<Long> findRelatedEvents(UserActionAvro action) {
        List<Long> relatedEvents = new ArrayList<>();

        for (Map.Entry<Long, Map<Long, Double>> entry : eventUserWeights.entrySet()) {
            Long eventId = entry.getKey();

            if (eventId.equals(action.getEventId())) {
                continue;
            }

            Double weight = entry.getValue().get(action.getUserId());

            if (weight != null && weight > 0) {
                relatedEvents.add(eventId);
            }
        }

        log.info("Related events for calculation: {}", relatedEvents);

        return relatedEvents;
    }

    private List<EventSimilarityAvro> buildSimilarities(UserActionAvro action, List<Long> relatedEventIds) {
        List<EventSimilarityAvro> result = new ArrayList<>();

        for (Long relatedEventId : relatedEventIds) {
            Long first = Math.min(action.getEventId(), relatedEventId);
            Long second = Math.max(action.getEventId(), relatedEventId);

            double similarity = calculateSimilarity(first, second);

            log.info("Similarity between events {} and {}: {}", first, second, similarity);

            result.add(EventSimilarityAvro.newBuilder()
                    .setEventA(first)
                    .setEventB(second)
                    .setScore(similarity)
                    .setTimestamp(action.getTimestamp())
                    .build());
        }

        return result;
    }

    private double calculateSimilarity(Long first, Long second) {
        double minSum = eventMinSums.get(first).get(second);
        double firstWeight = Math.sqrt(eventWeightSums.get(first));
        double secondWeight = Math.sqrt(eventWeightSums.get(second));

        return minSum / (firstWeight * secondWeight);
    }

    private void updateEventWeightSum(UserActionAvro action, double weightDiff) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();

        double currentWeight = eventUserWeights.get(eventId).getOrDefault(userId, 0.0);

        log.info("Updating total weight for event {}", eventId);

        eventWeightSums.merge(
                eventId,
                currentWeight == weightDiff ? currentWeight : weightDiff,
                Double::sum
        );

        log.info("Updated event weight sum for event {}: {}", eventId, eventWeightSums.get(eventId));
    }

    private void updateEventMinSums(UserActionAvro action, double weightDiff, List<Long> relatedEventIds) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();

        double currentWeight = getUserWeight(eventId, userId);
        double previousWeight = currentWeight - weightDiff;

        log.info("Updating min sums for event {}", eventId);

        for (Long relatedEventId : relatedEventIds) {
            double relatedWeight = getUserWeight(relatedEventId, userId);

            if (relatedWeight == 0.0) {
                continue;
            }

            Long first = Math.min(eventId, relatedEventId);
            Long second = Math.max(eventId, relatedEventId);

            double updatedSum = calculateUpdatedMinSum(
                    first,
                    second,
                    currentWeight,
                    previousWeight,
                    relatedWeight
            );

            eventMinSums.computeIfAbsent(first, key -> new HashMap<>()).put(second, updatedSum);

            log.info("Updated min sum for events {} and {}: {}", first, second, updatedSum);
        }
    }

    private double calculateUpdatedMinSum(Long first,
                                          Long second,
                                          double currentWeight,
                                          double previousWeight,
                                          double relatedWeight) {

        Double existingSum = eventMinSums
                .computeIfAbsent(first, key -> new HashMap<>())
                .get(second);

        if (existingSum == null) {
            return Math.min(currentWeight, relatedWeight);
        }

        if (currentWeight >= relatedWeight) {
            if (previousWeight >= relatedWeight) {
                return existingSum;
            }

            return existingSum + (relatedWeight - previousWeight);
        }

        return existingSum + (currentWeight - previousWeight);
    }

    private double calculateWeightDiff(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();

        double newWeight = getWeight(action.getActionType());
        double currentWeight = getUserWeight(eventId, userId);

        log.info("Calculating weight difference for event {} and user {}", eventId, userId);

        if (currentWeight >= newWeight) {
            return 0.0;
        }

        return newWeight - currentWeight;
    }

    private void updateUserWeight(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();

        double newWeight = getWeight(action.getActionType());
        double currentWeight = getUserWeight(eventId, userId);

        if (currentWeight >= newWeight) {
            log.info("Current weight {} is greater than or equal to new weight {}", currentWeight, newWeight);
            return;
        }

        eventUserWeights.computeIfAbsent(eventId, key -> new HashMap<>()).put(userId, newWeight);

        log.info("Updated user weight for event {} and user {}: {}", eventId, userId, newWeight);
    }

    private double getUserWeight(Long eventId, Long userId) {
        return eventUserWeights
                .getOrDefault(eventId, Map.of())
                .getOrDefault(userId, 0.0);
    }

    private double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> viewWeight;
            case REGISTER -> registerWeight;
            case LIKE -> likeWeight;
        };
    }
}