package ru.practicum.ewm.main.dao.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.Compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationDao {
    Compilation save(Compilation compilation);

    void deleteById(Long id);

    Optional<Compilation> findById(Long id);

    Page<Compilation> findAll(Pageable pageable);

    List<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}
