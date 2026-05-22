package ru.practicum.ewm.main.service.category;

import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryRequest;
import ru.practicum.ewm.main.dto.category.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryRequest request);

    CategoryDto updateCategory(Long catId, UpdateCategoryRequest request);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long id);

    void deleteCategory(Long catId);
}
