package ru.practicum.ewm.main.dto.category;

import ru.practicum.ewm.main.model.Category;

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