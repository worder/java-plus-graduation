package ru.practicum.ewm.stats.analyzer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.analyzer.model.UserAction;
import ru.practicum.ewm.stats.analyzer.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.analyzer.repository.UserActionRepository;
import ru.practicum.ewm.stats.analyzer.repository.projection.EventWeightSumProjection;
import ru.practicum.ewm.stats.messages.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.messages.RecommendedEventProto;
import ru.practicum.ewm.stats.messages.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.messages.UserPredictionsRequestProto;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationsHandlerImpl implements RecommendationsHandler {
    private final UserActionRepository actionRepository;
    private final EventSimilarityRepository similarityRepository;

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        long userId = request.getUserId();
        int limit = request.getMaxResults();

        // 1. Load user actions
        List<UserAction> userActions = actionRepository.findAllByUserId(
                userId,
                PageRequest.of(0, limit * 5, Sort.by(Sort.Direction.DESC, "timestamp")));

        if (userActions.isEmpty()) {
            return List.of();
        }

        // 2. Events the user interacted with
        Set<Long> interactedEventIds = userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        // 3. User action weights
        Map<Long, Double> actionWeights = userActions.stream()
                .collect(Collectors.toMap(
                        UserAction::getEventId,
                        UserAction::getWeight,
                        Double::max
                ));

        // 4. Load similarities
        List<EventSimilarity> similarities =
                similarityRepository.findAllByEventAInOrEventBIn(
                        interactedEventIds,
                        interactedEventIds,
                        PageRequest.of(0, limit * 20, Sort.by(Sort.Direction.DESC, "score"))
                );

        if (similarities.isEmpty()) {
            return List.of();
        }

        // 5. Map: recommendedEventId -> list of similarities related to viewed events
        Map<Long, List<EventSimilarity>> similaritiesMap = new HashMap<>();

        for (EventSimilarity similarity : similarities) {
            Long eventA = similarity.getEventA();
            Long eventB = similarity.getEventB();

            boolean aViewed = interactedEventIds.contains(eventA);
            boolean bViewed = interactedEventIds.contains(eventB);

            // A viewed -> B is a candidate
            if (aViewed && !bViewed) {
                similaritiesMap
                        .computeIfAbsent(eventB, k -> new ArrayList<>())
                        .add(similarity);
            }

            // B viewed -> A is a candidate
            if (bViewed && !aViewed) {
                similaritiesMap
                        .computeIfAbsent(eventA, k -> new ArrayList<>())
                        .add(similarity);
            }
        }

        // 6. Calculate the recommendation score
        return similaritiesMap.entrySet().stream()
                .map(entry -> {
                    Long recommendedEventId = entry.getKey();
                    Double score = calculateScore(
                            entry.getValue(),
                            interactedEventIds,
                            actionWeights
                    );

                    return RecommendedEventProto.newBuilder()
                            .setEventId(recommendedEventId)
                            .setScore(score)
                            .build();
                })
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .limit(limit)
                .toList();
    }

    /**
     * Calculates the weighted recommendation score for a candidate event.
     * Score = (Sum of (ActionWeight * SimilarityScore)) / (Sum of SimilarityScores)
     */
    private Double calculateScore(
            List<EventSimilarity> similarities,
            Set<Long> interactedEventIds,
            Map<Long, Double> actionWeights
    ) {
        double weightedSum = 0.0;
        double scoreSum = 0.0;

        for (EventSimilarity similarity : similarities) {
            long relatedViewedEventId;

            // Determine which event was viewed by the user
            if (interactedEventIds.contains(similarity.getEventA())) {
                relatedViewedEventId = similarity.getEventA();
            } else {
                relatedViewedEventId = similarity.getEventB();
            }

            Double actionWeight = actionWeights.get(relatedViewedEventId);
            if (actionWeight == null) {
                continue;
            }

            double similarityScore = similarity.getScore();

            weightedSum += actionWeight * similarityScore;
            scoreSum += similarityScore;
        }

        if (scoreSum == 0.0) {
            return 0.0;
        }

        return weightedSum / scoreSum;
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        Set<Long> eventIds = new HashSet<>(request.getEventIdList());

        if (eventIds.isEmpty()) {
            return List.of();
        }

        // Map event IDs to their total aggregated weights
        Map<Long, Double> weightsMap = actionRepository.sumWeightsByEventIds(eventIds)
                .stream()
                .collect(Collectors.toMap(
                        EventWeightSumProjection::getEventId,
                        projection -> Optional.ofNullable(projection.getTotalWeight())
                                .orElse(0.0)
                ));

        // Create proto list sorted by score (weight) descending
        return eventIds.stream()
                .map(eventId -> RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(weightsMap.getOrDefault(eventId, 0.0))
                        .build())
                .sorted(Comparator.comparing(RecommendedEventProto::getScore)
                        .reversed())
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        long eventId = request.getEventId();
        long userId = request.getUserId();
        int limit = request.getMaxResults();

        // 1. All events the user has visited before (Viewed events)
        Set<Long> viewedEventIds = actionRepository.findAllByUserId(userId)
                .stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());


        // 2. Load similarities.
        // We search for all connections where eventId is either A or B.
        List<EventSimilarity> similarities = similarityRepository.findAllByEventId(
                eventId,
                PageRequest.of(0, limit * 5, Sort.by(Sort.Direction.DESC, "score"))
        );

        if (similarities.isEmpty()) {
            return List.of();
        }

        // 3. Remove duplicates and filter:
        // eventId -> max score
        Map<Long, Double> recommendationsMap = new HashMap<>();

        for (EventSimilarity similarity : similarities) {
            long candidateEventId;

            if (similarity.getEventA().equals(eventId)) {
                candidateEventId = similarity.getEventB();
            } else if (similarity.getEventB().equals(eventId)) {
                candidateEventId = similarity.getEventA();
            } else {
                // Should not happen if the repository query is correct
                continue;
            }

            // Check: Do not recommend an event the user has already viewed
            if (viewedEventIds.contains(candidateEventId) || candidateEventId == eventId) {
                continue;
            }

            // Merge strategy: keep the highest score found for this candidate ID
            recommendationsMap.merge(
                    candidateEventId,
                    similarity.getScore(),
                    Math::max
            );
        }

        // 4. Assemble the result list
        return recommendationsMap.entrySet().stream()
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .limit(limit)
                .toList();
    }

}
