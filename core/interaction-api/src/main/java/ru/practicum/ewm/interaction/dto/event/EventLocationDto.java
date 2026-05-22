package ru.practicum.ewm.interaction.dto.event;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventLocationDto {
    Double lat;
    Double lon;
}
