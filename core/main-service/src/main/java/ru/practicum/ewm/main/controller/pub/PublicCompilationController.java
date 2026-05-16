package ru.practicum.ewm.main.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
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
