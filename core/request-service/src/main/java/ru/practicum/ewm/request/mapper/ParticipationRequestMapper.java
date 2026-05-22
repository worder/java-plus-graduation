package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.interaction.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.request.model.ParticipationRequest;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest model) {
        return ParticipationRequestDto.builder()
                .id(model.getId())
                .event(model.getEventId())
                .requester(model.getRequesterId())
                .status(model.getStatus().name())
                .created(model.getCreatedOn())
                .build();
    }
}
