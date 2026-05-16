package ru.practicum.ewm.main.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(@RequestParam Long eventId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getByEventId(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId) {
        return commentService.getById(commentId);
    }
}
