package ru.practicum.ewm.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.interaction.enums.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requesterId;

    private Long eventId;

    @Enumerated(EnumType.STRING)
    private ParticipationRequestStatus status;

    private LocalDateTime createdOn;
}
