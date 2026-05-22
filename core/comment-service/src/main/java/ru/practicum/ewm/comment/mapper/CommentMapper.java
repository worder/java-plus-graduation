package ru.practicum.ewm.comment.mapper;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.interaction.dto.comment.CommentDto;
import ru.practicum.ewm.interaction.dto.comment.NewCommentRequest;
import ru.practicum.ewm.interaction.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.interaction.dto.user.UserDto;
import ru.practicum.ewm.interaction.dto.user.UserShortDto;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CommentMapper {
    public static Comment mapToComment(NewCommentRequest request, Long authorId, Long eventId) {
        Comment comment = new Comment();

        comment.setText(request.getText());
        comment.setAuthorId(authorId);
        comment.setEventId(eventId);
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment, UserDto author) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setAuthor(UserShortDto.builder()
                .id(author.getId())
                .name(author.getName())
                .build());
        commentDto.setEventId(comment.getEventId());
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
