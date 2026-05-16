package ru.practicum.ewm.stat.dto;

import ru.practicum.ewm.stat.model.StatEvent;

public class StatEventMapper {
    public static StatEvent mapToModel(StatEventCreateDto request) {
        StatEvent event = new StatEvent();
        event.setApp(request.getApp());
        event.setUri(request.getUri());
        event.setIp(request.getIp());
        event.setDate(request.getTimestamp());
        return event;
    }
}
