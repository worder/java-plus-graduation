package ru.practicum.ewm.main.dao.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.model.Comment;

@Repository
public interface CommentDatabaseDao extends CommentDao, JpaRepository<Comment, Long> {
}
