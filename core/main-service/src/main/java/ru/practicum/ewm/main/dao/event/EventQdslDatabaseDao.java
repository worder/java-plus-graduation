package ru.practicum.ewm.main.dao.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.QEvent;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventQdslDatabaseDao implements EventQdslDao {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Event> findAllByParams(List<Long> users,
                                       List<Event.EventState> states,
                                       List<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Pageable pageable) {

        QEvent event = QEvent.event;

        BooleanBuilder builder = new BooleanBuilder();

        if (users != null && !users.isEmpty()) {
            builder.and(event.user.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            builder.and(event.state.in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            builder.and(event.category.id.in(categories));
        }

        if (rangeStart != null) {
            builder.and(event.eventDate.goe(rangeStart));
        }

        if (rangeEnd != null) {
            builder.and(event.eventDate.loe(rangeEnd));
        }

        return queryFactory
                .selectFrom(event)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Event> findAllPublicByParams(String text,
                                             List<Long> categories,
                                             Boolean paid,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Pageable pageable) {

        QEvent event = QEvent.event;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(event.state.eq(Event.EventState.PUBLISHED));

        if (text != null && !text.isBlank()) {
            String likePattern = "%" + text.toLowerCase() + "%";
            builder.and(
                    event.annotation.lower().like(likePattern)
                            .or(event.description.lower().like(likePattern))
            );
        }

        if (categories != null && !categories.isEmpty()) {
            builder.and(event.category.id.in(categories));
        }

        if (paid != null) {
            builder.and(event.paid.eq(paid));
        }

        if (rangeStart != null) {
            builder.and(event.eventDate.goe(rangeStart));
        }

        if (rangeEnd != null) {
            builder.and(event.eventDate.loe(rangeEnd));
        }

        return queryFactory
                .selectFrom(event)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}