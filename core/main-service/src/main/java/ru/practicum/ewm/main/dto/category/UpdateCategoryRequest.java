package ru.practicum.ewm.main.dto.category;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateCategoryRequest {
    @Size(min = 1, max = 50)
    String name;
}
