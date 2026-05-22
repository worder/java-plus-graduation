package ru.practicum.ewm.main.dto.event;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventLocationDto {
    Double lat;
    Double lon;
}
