package ru.practicum.ewm.main.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@AllArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(@RequestParam(required = false) List<Long> ids,
                                        @RequestParam(required = false) @Positive Long userId,
                                        @RequestParam(required = false) @Positive Long eventId,
                                        @RequestParam(required = false) @PastOrPresent LocalDateTime rangeStart,
                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return commentService.getByParams(ids, userId, eventId, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId,
                                    @RequestBody @Valid UpdateCommentRequest request) {
        return commentService.updateByAdmin(commentId, request);
    }

    @DeleteMapping
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteByAdmin(commentId);
    }
}
