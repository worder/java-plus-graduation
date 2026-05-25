package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.event.model.Category;
import ru.practicum.ewm.interaction.dto.category.CategoryDto;
import ru.practicum.ewm.interaction.dto.category.NewCategoryRequest;
import ru.practicum.ewm.interaction.dto.category.UpdateCategoryRequest;

public class CategoryMapper {
    public static CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category mapToEntity(NewCategoryRequest dto) {
        Category cat = new Category();
        cat.setName(dto.getName());
        return cat;
    }

    public static void updateEntity(Category category, UpdateCategoryRequest dto) {
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
    }
}