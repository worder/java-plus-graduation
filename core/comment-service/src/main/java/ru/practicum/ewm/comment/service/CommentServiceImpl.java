package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentDao;
import ru.practicum.ewm.comment.repository.CommentsQdslDao;
import ru.practicum.ewm.interaction.client.event.EventClient;
import ru.practicum.ewm.interaction.client.request.RequestClient;
import ru.practicum.ewm.interaction.client.user.UserClient;
import ru.practicum.ewm.interaction.dto.comment.CommentDto;
import ru.practicum.ewm.interaction.dto.comment.NewCommentRequest;
import ru.practicum.ewm.interaction.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.interaction.dto.event.EventFullDto;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.interaction.dto.user.UserDto;
import ru.practicum.ewm.interaction.enums.EventState;
import ru.practicum.ewm.interaction.enums.ParticipationRequestStatus;
import ru.practicum.ewm.interaction.error.exception.BadRequestException;
import ru.practicum.ewm.interaction.error.exception.ConflictException;
import ru.practicum.ewm.interaction.error.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final CommentsQdslDao commentQdslDao;

    private final UserClient userClient;
    private final EventClient eventClient;
    private final RequestClient requestClient;

    @Override
    public CommentDto create(Long userId, NewCommentRequest request) {
        UserDto author = userClient.getUser(userId);
        EventFullDto event = eventClient.getEvent(request.getEventId());
        ParticipationRequestDto pr = requestClient.getEventParticipationRequest(userId, event.getId());

        if (!ParticipationRequestStatus.CONFIRMED.name().equals(pr.getStatus())) {
            throw new ConflictException("User participation request are not approved");
        }

        if (event.getState() != EventState.PUBLISHED
                && event.getEventDate().plusHours(1).isAfter(LocalDateTime.now())) {
            throw new ConflictException("Comments can be created 1 hour after the event date");
        }

        log.debug("Creating comment from dto: {}; userId: {}", request, userId);
        Comment comment = CommentMapper.mapToComment(request, author.getId(), event.getId());
        Comment createdComment = commentDao.save(comment);
        log.info("Created comment: {}; from dto: {}, userId: {}", createdComment, request, userId);

        return CommentMapper.mapToCommentDto(createdComment, author);
    }

    @Override
    public CommentDto update(Long userId, Long commentId, UpdateCommentRequest request) {
        Comment comment = this.getAuthorCommentOrThrow(userId, commentId);
        UserDto author = userClient.getUser(userId);

        log.debug("Updating comment Id:{} from dto: {}, userId: {}", commentId, request, userId);
        Comment updatedComment = CommentMapper.updateComment(comment, request);
        log.info("Updated comment: {}; from dto: {}", comment, request);

        return CommentMapper.mapToCommentDto(commentDao.save(updatedComment), author);
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
        UserDto author = userClient.getUser(authorId);

        return commentDao.findByAuthorId(authorId, page).stream()
                .map(c -> CommentMapper.mapToCommentDto(c, author))
                .toList();
    }

    @Override
    public List<CommentDto> getByEventId(Long eventId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        List<Comment> comments = commentDao.findByEventId(eventId, page);
        Map<Long, UserDto> authors = getAuthorsDto(comments);

        return comments
                .stream()
                .map(c -> CommentMapper.mapToCommentDto(c, authors.get(c.getAuthorId())))
                .toList();
    }

    @Override
    public CommentDto getByAuthorIdAndId(Long userId, Long commentId) {
        Comment comment = this.getAuthorCommentOrThrow(userId, commentId);
        UserDto author = userClient.getUser(comment.getAuthorId());
        return CommentMapper.mapToCommentDto(comment, author);
    }

    public CommentDto getById(Long commentId) {
        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment ID:" + commentId + " not found"));
        UserDto author = userClient.getUser(comment.getAuthorId());
        return CommentMapper.mapToCommentDto(comment, author);
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
        List<Comment> comments = commentQdslDao.findCommentsByParams(ids, userId, eventId, rangeStart, rangeEnd, page);
        Map<Long, UserDto> authors = getAuthorsDto(comments);

        return comments
                .stream()
                .map(c -> CommentMapper.mapToCommentDto(c, authors.get(c.getAuthorId())))
                .toList();
    }

    @Override
    public CommentDto updateByAdmin(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment Id:" + commentId + " not found"));
        UserDto author = userClient.getUser(comment.getAuthorId());

        log.debug("Updating comment by admin, Id:{} from dto: {}", commentId, request);
        Comment updatedComment = CommentMapper.updateComment(comment, request);
        log.info("Updated comment by admin: {}; from dto: {}", comment, request);

        return CommentMapper.mapToCommentDto(commentDao.save(updatedComment), author);
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

        if (comment.getAuthorId().equals(userId)) {
            throw new BadRequestException("Invalid user id:" + comment.getAuthorId() + "; user not author");
        }

        return comment;
    }

    private Map<Long, UserDto> getAuthorsDto(List<Comment> comments) {
        List<Long> authorsId = comments.stream().map(Comment::getAuthorId).toList();
        if (authorsId.isEmpty()) {
            return Map.of();
        }

        return userClient.getUsers(authorsId, 0, authorsId.size())
                .stream().collect(Collectors.toMap(UserDto::getId, u -> u));
    }
}
