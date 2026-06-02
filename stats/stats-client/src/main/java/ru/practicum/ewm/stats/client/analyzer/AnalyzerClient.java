package ru.practicum.ewm.stats.client.analyzer;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.messages.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.messages.RecommendedEventProto;
import ru.practicum.ewm.stats.messages.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.messages.UserPredictionsRequestProto;
import ru.practicum.ewm.stats.services.RecommendationsControllerGrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AnalyzerClient {
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerClient;

    public List<RecommendedEventProto> getRecommendations(Long userId, Integer maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        log.info("Requesting recommendations for user {}: {}", userId, request);

        List<RecommendedEventProto> result = new ArrayList<>();
        analyzerClient.getRecommendationsForUser(request)
                .forEachRemaining(result::add);

        log.info("Successfully retrieved recommendations: {}", result);
        return result;
    }

    public List<RecommendedEventProto> getSimilarEvents(Long eventId, Long userId, Integer maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        log.info("Requesting similar events for event {} and user {}: {}", eventId, userId, request);

        List<RecommendedEventProto> result = new ArrayList<>();
        analyzerClient.getSimilarEvents(request)
                .forEachRemaining(result::add);

        log.info("Successfully retrieved similar events: {}", result);
        return result;
    }

    public Map<Long, Double> getInteractionsCount(List<Long> eventIds) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        log.info("Requesting interaction scores for events: {}", request);

        Map<Long, Double> result = new HashMap<>();
        analyzerClient.getInteractionsCount(request)
                .forEachRemaining(e -> result.put(e.getEventId(), e.getScore()));

        log.info("Successfully retrieved interaction scores: {}", result);
        return result;
    }
}
