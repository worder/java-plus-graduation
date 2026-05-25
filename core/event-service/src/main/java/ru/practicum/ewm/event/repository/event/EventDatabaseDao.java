package ru.practicum.ewm.event.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

public interface EventDatabaseDao extends EventDao, JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
}