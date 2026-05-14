package ru.practicum.ewm.main.controller.pvt;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentRequest;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@AllArgsConstructor
public class PvtUserCommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@PathVariable Long userId,
                                    @RequestBody @Valid NewCommentRequest request) {
        return commentService.create(userId, request);
    }

    @GetMapping
    public List<CommentDto> getComments(@PathVariable Long userId,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getByAuthorId(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long userId,
                                 @PathVariable Long commentId) {
        return commentService.getByAuthorIdAndId(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody @Valid UpdateCommentRequest request) {
        return commentService.update(userId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.delete(userId, commentId);
    }
}
