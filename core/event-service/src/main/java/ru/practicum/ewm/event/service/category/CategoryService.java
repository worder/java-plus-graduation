package ru.practicum.ewm.event.service.category;

import ru.practicum.ewm.interaction.dto.category.CategoryDto;
import ru.practicum.ewm.interaction.dto.category.NewCategoryRequest;
import ru.practicum.ewm.interaction.dto.category.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryRequest request);

    CategoryDto updateCategory(Long catId, UpdateCategoryRequest request);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long id);

    void deleteCategory(Long catId);
}
