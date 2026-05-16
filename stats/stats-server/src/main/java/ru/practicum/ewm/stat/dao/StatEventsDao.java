package ru.practicum.ewm.stat.dao;

import ru.practicum.ewm.stat.model.StatEvent;

public interface StatEventsDao {
    StatEvent save(StatEvent statEvent);
}
