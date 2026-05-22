package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentsQdslDao {
    List<Comment> findCommentsByParams(List<Long> ids,
                                       Long userId,
                                       Long eventId,
                                       LocalDateTime start,
                                       LocalDateTime end,
                                       Pageable pageable);
}
