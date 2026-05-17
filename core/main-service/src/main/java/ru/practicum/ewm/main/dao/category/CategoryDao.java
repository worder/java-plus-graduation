package ru.practicum.ewm.main.dao.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.Category;

import java.util.Optional;

public interface CategoryDao {
    Category save(Category category);

    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);

    Page<Category> findAll(Pageable pageable);

    void deleteById(Long id);
}
