package ru.practicum.ewm.event.controller.complilation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.service.compilation.CompilationService;
import ru.practicum.ewm.interaction.dto.compilation.CompilationDto;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
@Validated
public class PublicCompilationController {
    CompilationService compilationService;

    @GetMapping
    List<CompilationDto> getCompilations(@RequestParam(required = false) boolean pinned,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        return compilationService.getCompilations(from, size, pinned);
    }

    @GetMapping("/{id}")
    CompilationDto getCompilation(@PathVariable Long id) {
        return compilationService.getCompilation(id);
    }
}
