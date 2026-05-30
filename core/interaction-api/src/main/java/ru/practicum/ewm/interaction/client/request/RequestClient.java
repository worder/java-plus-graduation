package ru.practicum.ewm.interaction.client.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestDto;

@FeignClient(
        name = "request-service",
        configuration = RequestClientConfig.class)
public interface RequestClient {

    @GetMapping("/users/{userId}/events/{eventId}/request")
    ParticipationRequestDto getEventParticipationRequest(@PathVariable Long userId,
                                                         @PathVariable Long eventId);
}
