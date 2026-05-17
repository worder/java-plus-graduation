package ru.practicum.ewm.main.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.comment.CommentDao;
import ru.practicum.ewm.main.dao.comment.CommentsQdslDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dao.request.ParticipationRequestDao;
import ru.practicum.ewm.main.dao.user.UserDao;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.CommentMapper;
import ru.practicum.ewm.main.dto.comment.NewCommentRequest;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.main.error.exception.BadRequestException;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.ParticipationRequest;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final CommentsQdslDao commentQdslDao;
    private final UserDao userDao;
    private final EventDao eventDao;
    private final ParticipationRequestDao participationRequestDao;

    @Override
    public CommentDto create(Long userId, NewCommentRequest request) {
        User author = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Id:" + userId + " not found"));

        Event event = eventDao.findById(request.getEventId())
                .orElseThrow(() -> new NotFoundException("Event Id:" + request.getEventId() + " not found"));

        ParticipationRequest pr = participationRequestDao.findByEventIdAndRequesterId(request.getEventId(), userId)
                .orElseThrow(() -> new NotFoundException("User has not participated in the event"));

        if (pr.getStatus() != ParticipationRequest.Status.CONFIRMED) {
            throw new ConflictException("User participation request are not approved");
        }

        if (event.getState() != Event.EventState.PUBLISHED
                && event.getEventDate().plusHours(1).isAfter(LocalDateTime.now())) {
            throw new ConflictException("Comments can be created 1 hour after the event date");
        }

        log.debug("Creating comment from dto: {}; userId: {}", request, userId);
        Comment comment = CommentMapper.mapToComment(request, author, event);
        Comment createdComment = commentDao.save(comment);
        log.info("Created comment: {}; from dto: {}, userId: {}", createdComment, request, userId);

        return CommentMapper.mapToCommentDto(createdComment);
    }

    @Override
    public CommentDto update(Long userId, Long commentId, UpdateCommentRequest request) {
        Comment comment = this.getAuthorCommentOrThrow(userId, commentId);

        log.debug("Updating comment Id:{} from dto: {}, userId: {}", commentId, request, userId);
        Comment updatedComment = CommentMapper.updateComment(comment, request);
        log.info("Updated comment: {}; from dto: {}", comment, request);

        return CommentMapper.mapToCommentDto(commentDao.save(updatedComment));
    }

    @Override
    public void delete(Long userId, Long commentId) {
        if (!commentDao.existsByAuthorIdAndId(userId, commentId)) {
            throw new NotFoundException("Comment Id:" + commentId + " not found");
        }

        this.commentDao.deleteById(commentId);
        log.info("Deleted comment, Id:{}; userId: {}", commentId, userId);
    }

    @Override
    public List<CommentDto> getByAuthorId(Long authorId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        return commentDao.findByAuthorId(authorId, page).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    public List<CommentDto> getByEventId(Long eventId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        return commentDao.findByEventId(eventId, page).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    public CommentDto getByAuthorIdAndId(Long userId, Long commentId) {
        return CommentMapper.mapToCommentDto(this.getAuthorCommentOrThrow(userId, commentId));
    }

    public CommentDto getById(Long commentId) {
        return commentDao.findById(commentId)
                .map(CommentMapper::mapToCommentDto)
                .orElseThrow(() -> new NotFoundException("Comment ID:" + commentId + " not found"));
    }

    @Override
    public List<CommentDto> getByParams(List<Long> ids,
                                        Long userId,
                                        Long eventId,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size
    ) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Invalid date range, rangeEnd should be greater than rangeStart");
        }

        Pageable page = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        return commentQdslDao.findCommentsByParams(ids, userId, eventId, rangeStart, rangeEnd, page)
                .stream()
                .map(CommentMapper::mapToCommentDto).toList();
    }

    @Override
    public CommentDto updateByAdmin(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Id:" + commentId + " not found"));

        log.debug("Updating comment by admin, Id:{} from dto: {}", commentId, request);
        Comment updatedComment = CommentMapper.updateComment(comment, request);
        log.info("Updated comment by admin: {}; from dto: {}", comment, request);

        return CommentMapper.mapToCommentDto(commentDao.save(updatedComment));
    }

    @Override
    public void deleteByAdmin(Long commentId) {
        if (!commentDao.existsById(commentId)) {
            throw new NotFoundException("Comment ID:" + commentId + " not found");
        }

        commentDao.deleteById(commentId);
        log.info("Deleted comment by admin, Id:{}", commentId);
    }

    private Comment getAuthorCommentOrThrow(Long userId, Long commentId) {
        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment ID:" + commentId + " not found"));

        if (comment.getAuthor().getId().equals(userId)) {
            throw new BadRequestException("Invalid user id:" + comment.getAuthor().getId() + "; user not author");
        }

        return comment;
    }
}
