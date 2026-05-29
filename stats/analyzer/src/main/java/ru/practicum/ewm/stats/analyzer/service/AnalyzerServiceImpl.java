package ru.practicum.ewm.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.analyzer.mapper.EventSimilarityMapper;
import ru.practicum.ewm.stats.analyzer.mapper.UserActionMapper;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.analyzer.model.UserAction;
import ru.practicum.ewm.stats.analyzer.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.analyzer.repository.UserActionRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalyzerServiceImpl implements AnalyzerService {
    private final EventSimilarityRepository similarityRepository;
    private final UserActionRepository actionRepository;
    private final UserActionMapper actionMapper;

    @Override
    public void analyzeEventSimilarity(EventSimilarityAvro similarityAvro) {
        EventSimilarity similarity = EventSimilarityMapper.mapToEventSimilarity(similarityAvro);

        // Check if this similarity pair exists
        if (!similarityRepository.existsByEventAAndEventB(similarity.getEventA(), similarity.getEventB())) {
            similarity = similarityRepository.save(similarity);
            log.info("Saving new similarity: {}", similarity);
        } else {
            // Fetch the existing entity (assuming it exists since we checked)
            EventSimilarity oldSimilarity = similarityRepository
                    .findByEventAAndEventB(similarity.getEventA(), similarity.getEventB()).get();

            log.info("Finding old similarity in DB: {}", oldSimilarity);

            // Business logic: Only update if the new score is greater
            if (similarity.getScore() > oldSimilarity.getScore()) {
                oldSimilarity.setScore(similarity.getScore());
                oldSimilarity.setTimestamp(similarity.getTimestamp());
                oldSimilarity = similarityRepository.save(oldSimilarity);
                log.info("Similarity increased, updating in DB: {}", oldSimilarity);
            } else {
                log.info("Similarity did not increase, no need to update");
            }
        }
    }

    @Override
    public void analyzeUserAction(UserActionAvro actionAvro) {
        UserAction action = actionMapper.mapToUserAction(actionAvro);

        // Check if this action pair exists
        if (!actionRepository.existsByEventIdAndUserId(action.getEventId(), action.getUserId())) {
            action = actionRepository.save(action);
            log.info("Saving new action: {}", action);
        } else {
            // Fetch the existing entity
            UserAction oldAction = actionRepository
                    .findByEventIdAndUserId(action.getEventId(), action.getUserId()).get();

            log.info("Finding old action in DB: {}", oldAction);

            // Business logic: Only update if the new weight is greater
            if (action.getWeight() > oldAction.getWeight()) {
                oldAction.setWeight(action.getWeight());
                oldAction.setTimestamp(action.getTimestamp());
                oldAction = actionRepository.save(oldAction);
                log.info("Action weight increased, updating in DB: {}", oldAction);
            } else {
                log.info("Action weight did not increase, no need to update");
            }
        }
    }
}
