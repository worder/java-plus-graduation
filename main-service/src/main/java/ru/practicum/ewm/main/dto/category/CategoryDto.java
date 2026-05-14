package ru.practicum.ewm.main.dto.category;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryDto {
    Long id;

    String name;
}
