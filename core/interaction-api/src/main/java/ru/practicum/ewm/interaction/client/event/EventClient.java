package ru.practicum.ewm.interaction.client.event;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.interaction.dto.event.EventFullDto;
import ru.practicum.ewm.interaction.dto.event.UpdateEventAdminRequest;

@FeignClient(
        name = "event-service",
        configuration = EventClientConfig.class)
public interface EventClient {
    @GetMapping("/admin/events/{eventId}")
    EventFullDto getEvent(@PathVariable Long eventId);

    @PatchMapping("/admin/events/{eventId}")
    EventFullDto updateEvent(@PathVariable Long eventId,
                             @RequestBody @Valid UpdateEventAdminRequest request);
}
