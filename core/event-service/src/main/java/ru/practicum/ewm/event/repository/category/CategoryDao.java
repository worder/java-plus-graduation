package ru.practicum.ewm.event.repository.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    Category save(Category category);

    Optional<Category> findById(Long id);

    List<Category> findAllByIdIn(List<Long> ids);

    Optional<Category> findByName(String name);

    Page<Category> findAll(Pageable pageable);

    void deleteById(Long id);
}
