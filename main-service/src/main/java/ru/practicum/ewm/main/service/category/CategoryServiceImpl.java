package ru.practicum.ewm.main.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dao.category.CategoryDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.CategoryMapper;
import ru.practicum.ewm.main.dto.category.NewCategoryRequest;
import ru.practicum.ewm.main.dto.category.UpdateCategoryRequest;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Category;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;
    private final EventDao eventDao;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryRequest request) {
        categoryDao.findByName(request.getName()).ifPresent(c -> {
            throw new ConflictException("Category with name " + c.getName() + " already exists");
        });

        Category category = CategoryMapper.mapToEntity(request);
        return CategoryMapper.mapToDto(categoryDao.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, UpdateCategoryRequest request) {
        Category category = getCategoryOrThrow(catId);

        if (request.getName() != null) {
            Optional<Category> existing = categoryDao.findByName(request.getName());
            if (existing.isPresent() && !existing.get().getId().equals(catId)) {
                throw new ConflictException("Category with name '" + request.getName() + "' already exists.");
            }
            category.setName(request.getName());
        }

        return CategoryMapper.mapToDto(categoryDao.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (eventDao.countByCategoryId(catId) > 0) {
            throw new ConflictException("Can not delete category with events");
        }

        getCategoryOrThrow(catId);
        categoryDao.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryDao.findAll(pageable).getContent().stream().map(CategoryMapper::mapToDto).toList();
    }

    @Override
    public CategoryDto getCategory(Long id) {
        return categoryDao.findById(id)
                .map(CategoryMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found."));
    }

    private Category getCategoryOrThrow(Long catId) {
        return categoryDao.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found."));
    }
}
