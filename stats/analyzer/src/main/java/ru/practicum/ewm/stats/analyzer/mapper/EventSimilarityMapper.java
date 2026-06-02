package ru.practicum.ewm.stats.analyzer.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@UtilityClass
public class EventSimilarityMapper {
    public static EventSimilarity mapToEventSimilarity(EventSimilarityAvro avro) {
        EventSimilarity eventSimilarity = new EventSimilarity();

        eventSimilarity.setEventA(avro.getEventA());
        eventSimilarity.setEventB(avro.getEventB());
        eventSimilarity.setScore(avro.getScore());
        eventSimilarity.setTimestamp(avro.getTimestamp());

        return eventSimilarity;
    }
}