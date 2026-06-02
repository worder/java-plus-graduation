package ru.practicum.ewm.stats.analyzer.repository.projection;

public interface EventWeightSumProjection {
    Long getEventId();

    Double getTotalWeight();
}