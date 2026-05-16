package ru.practicum.ewm.main.dao.comment;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.QComment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentsQdslDatabaseDao implements CommentsQdslDao {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findCommentsByParams(List<Long> ids,
                                              Long userId,
                                              Long eventId,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Pageable pageable) {
        QComment comment = QComment.comment;

        BooleanBuilder builder = new BooleanBuilder();

        if (ids != null && !ids.isEmpty()) {
            builder.and(comment.id.in(ids));
        }

        if (userId != null) {
            builder.and(comment.author.id.eq(userId));
        }

        if (eventId != null) {
            builder.and(comment.event.id.eq(eventId));
        }

        if (rangeStart != null) {
            builder.and(comment.createdOn.goe(rangeStart));
        }

        if (rangeEnd != null) {
            builder.and(comment.createdOn.loe(rangeEnd));
        }

        return queryFactory
                .selectFrom(comment)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
