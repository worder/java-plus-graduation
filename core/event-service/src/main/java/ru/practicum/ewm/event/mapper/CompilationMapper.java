package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.event.model.Compilation;
import ru.practicum.ewm.interaction.dto.compilation.CompilationDto;
import ru.practicum.ewm.interaction.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.interaction.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.interaction.dto.event.EventShortDto;

import java.util.List;

public class CompilationMapper {

    public static CompilationDto mapToDto(Compilation compilation, List<EventShortDto> events) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());
        dto.setEvents(events);
        return dto;
    }

    public static Compilation mapToCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());
        return compilation;
    }

    public static void updateCompilationFromRequest(Compilation compilation, UpdateCompilationRequest request) {
        if (request.getTitle() != null) compilation.setTitle(request.getTitle());
        if (request.getPinned() != null) compilation.setPinned(request.getPinned());
    }
}
