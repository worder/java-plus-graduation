package ru.practicum.ewm.main.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentRequest {
    @NotNull
    @Positive
    private Long eventId;

    @NotBlank
    @Size(min = 2, max = 1000)
    private String text;
}
