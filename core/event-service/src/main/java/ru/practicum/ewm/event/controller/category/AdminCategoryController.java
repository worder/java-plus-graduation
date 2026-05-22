package ru.practicum.ewm.event.controller.category;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.service.category.CategoryService;
import ru.practicum.ewm.interaction.dto.category.CategoryDto;
import ru.practicum.ewm.interaction.dto.category.NewCategoryRequest;
import ru.practicum.ewm.interaction.dto.category.UpdateCategoryRequest;

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
