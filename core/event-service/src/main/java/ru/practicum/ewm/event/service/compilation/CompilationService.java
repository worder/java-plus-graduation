package ru.practicum.ewm.event.service.compilation;

import ru.practicum.ewm.interaction.dto.compilation.CompilationDto;
import ru.practicum.ewm.interaction.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.interaction.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);

    List<CompilationDto> getCompilations(Integer from, Integer size, Boolean pinned);

    CompilationDto getCompilation(Long id);
}
