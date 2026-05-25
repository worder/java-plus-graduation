package ru.practicum.ewm.event.repository.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.model.Compilation;

public interface CompilationDatabaseDao extends CompilationDao, JpaRepository<Compilation, Long> {
}
