package ru.practicum.ewm.main.dto.comment;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequest {
    @Size(min = 2, max = 1000)
    private String text;
}
