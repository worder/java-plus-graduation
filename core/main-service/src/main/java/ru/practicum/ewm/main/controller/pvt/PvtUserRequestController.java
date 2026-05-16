package ru.practicum.ewm.main.controller.pvt;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.service.request.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class PvtUserRequestController {
    private final ParticipationRequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        return requestService.createEventParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
        return requestService.cancelEventParticipationRequest(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId) {
        return requestService.getUserParticipationRequests(userId);
    }
}
