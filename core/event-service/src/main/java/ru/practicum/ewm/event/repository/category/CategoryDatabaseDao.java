package ru.practicum.ewm.event.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.model.Category;

public interface CategoryDatabaseDao extends CategoryDao, JpaRepository<Category, Long> {
}
