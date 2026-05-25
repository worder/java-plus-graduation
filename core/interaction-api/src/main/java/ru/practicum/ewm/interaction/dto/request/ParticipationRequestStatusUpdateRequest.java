package ru.practicum.ewm.interaction.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ParticipationRequestStatusUpdateRequest {
    public enum ParticipationRequestStatusUpdate {
        CONFIRMED,
        REJECTED
    }

    private List<Long> requestIds;
    private ParticipationRequestStatusUpdate status;
}
