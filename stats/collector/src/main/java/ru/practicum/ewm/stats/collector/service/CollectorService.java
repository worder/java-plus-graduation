package ru.practicum.ewm.stats.collector.service;

import ru.practicum.ewm.stats.messages.UserActionProto;

public interface CollectorService {
    void collectUserAction(UserActionProto request);
}
