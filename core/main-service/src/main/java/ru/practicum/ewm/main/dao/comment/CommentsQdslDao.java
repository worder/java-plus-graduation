package ru.practicum.ewm.main.dao.comment;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.Comment;

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
