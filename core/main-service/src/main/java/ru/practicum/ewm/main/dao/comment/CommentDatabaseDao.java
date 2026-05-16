package ru.practicum.ewm.main.dao.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.Comment;

public interface CommentDatabaseDao extends CommentDao, JpaRepository<Comment, Long> {
}
