package ru.practicum.ewm.main.service.compilation;

import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);

    List<CompilationDto> getCompilations(Integer from, Integer size, Boolean pinned);

    CompilationDto getCompilation(Long id);
}
