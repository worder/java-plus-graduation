package ru.practicum.ewm.main.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.compilation.CompilationDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.CompilationMapper;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Compilation;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.service.event.EventService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationDao compilationDao;
    private final EventDao eventDao;
    private final EventService eventService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation compilation = CompilationMapper.mapToCompilation(dto);
        List<Event> events = new ArrayList<>();

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = eventDao.findAllByIdIn(dto.getEvents());
            if (events.size() != dto.getEvents().size()) {
                throw new NotFoundException("Одно или несколько событий не найдены");
            }
        }

        compilation.setEvents(events);
        Compilation saved = compilationDao.save(compilation);

        return CompilationMapper.mapToDto(saved, eventService.mapToShortDtos(saved.getEvents()));
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationDao.findById(compId).isPresent()) {
            throw new NotFoundException("Подборка с ID=" + compId + " не найдена");
        }
        compilationDao.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationDao.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с ID=" + compId + " не найдена"));

        CompilationMapper.updateCompilationFromRequest(compilation, request);

        if (request.getEvents() != null) {
            List<Event> events = eventDao.findAllByIdIn(request.getEvents());
            if (events.size() != request.getEvents().size()) {
                throw new NotFoundException("Одно или несколько событий не найдены");
            }
            compilation.setEvents(events);
        }

        Compilation updated = compilationDao.save(compilation);
        return CompilationMapper.mapToDto(updated, eventService.mapToShortDtos(updated.getEvents()));
    }

    @Override
    public List<CompilationDto> getCompilations(Integer from, Integer size, Boolean pinned) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> models;
        if (pinned != null) {
            models = compilationDao.findByPinned(pinned, pageable);
        } else {
            models = compilationDao.findAll(pageable).getContent();
        }

        return models.stream()
                .map(c -> CompilationMapper.mapToDto(c, eventService.mapToShortDtos(c.getEvents())))
                .toList();
    }

    @Override
    public CompilationDto getCompilation(Long id) {
        Compilation comp = compilationDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + id + " not found"));

        return CompilationMapper.mapToDto(comp, eventService.mapToShortDtos(comp.getEvents()));
    }
}
