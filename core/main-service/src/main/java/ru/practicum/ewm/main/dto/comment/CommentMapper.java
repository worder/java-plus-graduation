package ru.practicum.ewm.main.dto.comment;

import ru.practicum.ewm.main.dto.user.UserMapper;
import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment mapToComment(NewCommentRequest request, User author, Event event) {
        Comment comment = new Comment();

        comment.setText(request.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setAuthor(UserMapper.mapToShortDto(comment.getAuthor()));
        commentDto.setEventId(comment.getEvent().getId());
        commentDto.setText(comment.getText());
        commentDto.setCreatedOn(comment.getCreatedOn());
        commentDto.setEditedOn(comment.getEditedOn());
        return commentDto;
    }

    public static Comment updateComment(Comment comment, UpdateCommentRequest request) {
        if (request.getText() != null) {
            comment.setText(request.getText());
            comment.setEditedOn(LocalDateTime.now());
        }

        return comment;
    }
}
