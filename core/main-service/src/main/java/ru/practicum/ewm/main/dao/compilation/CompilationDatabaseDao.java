package ru.practicum.ewm.main.dao.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.Compilation;

public interface CompilationDatabaseDao extends CompilationDao, JpaRepository<Compilation, Long> {
}
