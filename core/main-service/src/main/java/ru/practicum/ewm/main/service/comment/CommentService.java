package ru.practicum.ewm.main.service.comment;

import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentRequest;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, NewCommentRequest request);

    CommentDto update(Long userId, Long commentId, UpdateCommentRequest request);

    void delete(Long userId, Long commentId);

    CommentDto getByAuthorIdAndId(Long userId, Long commentId);

    CommentDto getById(Long commentId);

    List<CommentDto> getByAuthorId(Long authorId, Integer from, Integer size);

    List<CommentDto> getByEventId(Long eventId, Integer from, Integer size);

    List<CommentDto> getByParams(List<Long> ids,
                                 Long userId,
                                 Long eventId,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 Integer from,
                                 Integer size);

    CommentDto updateByAdmin(Long commentId, UpdateCommentRequest request);

    void deleteByAdmin(Long commentId);
}
