package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "participation_requests")
public class ParticipationRequest {
    public enum Status {
        PENDING,
        CONFIRMED,
        CANCELED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requesterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdOn;
}
