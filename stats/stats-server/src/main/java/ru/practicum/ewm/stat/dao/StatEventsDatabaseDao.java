package ru.practicum.ewm.stat.dao;

import ru.practicum.ewm.stat.model.StatEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StatEventsDatabaseDao extends
        StatEventsDao,
        JpaRepository<StatEvent, Long>,
        QuerydslPredicateExecutor<StatEvent> {
}
