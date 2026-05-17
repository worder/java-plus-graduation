package ru.practicum.ewm.main.dao.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.main.model.Event;

public interface EventDatabaseDao extends EventDao, JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
}