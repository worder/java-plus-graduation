package ru.practicum.ewm.main.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryRequest;
import ru.practicum.ewm.main.dto.category.UpdateCategoryRequest;
import ru.practicum.ewm.main.service.category.CategoryService;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody @Valid UpdateCategoryRequest request) {
        return categoryService.updateCategory(catId, request);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }
}
