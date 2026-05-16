package ru.practicum.ewm.stat.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stat.dao.StatEventsDao;
import ru.practicum.ewm.stat.dto.StatEventCreateDto;
import ru.practicum.ewm.stat.dto.StatEventMapper;
import ru.practicum.ewm.stat.dto.StatEventViewDto;
import ru.practicum.ewm.stat.error.exception.BadRequestException;
import ru.practicum.ewm.stat.model.QStatEvent;
import ru.practicum.ewm.stat.model.StatEvent;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatEventsDao repository;
    private final JPAQueryFactory queryFactory;

    @Override
    public void createStatEvent(StatEventCreateDto eventRequest) {
        StatEvent model = StatEventMapper.mapToModel(eventRequest);
        log.info("Creating hit event from dto: {}", eventRequest);
        repository.save(model);
    }

    @Override
    public List<StatEventViewDto> findStatEvents(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique) {

        QStatEvent statEvent = QStatEvent.statEvent;

        if (end != null && start != null && end.isBefore(start)) {
            throw new BadRequestException("End date is before start date");
        }

        BooleanExpression searchConditions = QStatEvent.statEvent.date.between(start, end);

        if (!uris.isEmpty()) {
            searchConditions = searchConditions.and(QStatEvent.statEvent.uri.in(uris));
        }

        NumberExpression<Long> hitsExpression = unique
                ? statEvent.ip.countDistinct()
                : statEvent.ip.count();

        JPAQuery<StatEventViewDto> query = queryFactory.select(Projections.constructor(
                        StatEventViewDto.class,
                        statEvent.app,
                        statEvent.uri,
                        hitsExpression.as("hits")))
                .from(statEvent)
                .where(searchConditions)
                .groupBy(statEvent.app, statEvent.uri)
                .orderBy(hitsExpression.desc());

        return query.fetch();
    }
}
