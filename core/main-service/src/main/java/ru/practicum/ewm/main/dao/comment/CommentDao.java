package ru.practicum.ewm.main.dao.comment;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {
    Comment save(Comment comment);

    List<Comment> findByAuthorId(Long userId, Pageable pageable);

    List<Comment> findByEventId(Long eventId, Pageable pageable);

    Optional<Comment> findById(Long commentId);

    boolean existsById(Long commentId);

    boolean existsByAuthorIdAndId(Long authorId, Long commentId);

    void deleteById(Long commentId);
}
