package ru.practicum.ewm.interaction.dto.category;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryDto {
    Long id;

    String name;
}
