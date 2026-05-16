package ru.practicum.ewm.main.dao.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.Category;

public interface CategoryDatabaseDao extends CategoryDao, JpaRepository<Category, Long> {
}
