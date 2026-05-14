package ru.practicum.ewm.main.dto.compilation;

import ru.practicum.ewm.main.dto.event.EventShortDto;
import ru.practicum.ewm.main.model.Compilation;

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
