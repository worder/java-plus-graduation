package ru.practicum.ewm.main.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.service.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{id}")
    public CategoryDto getCategories(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }
}
