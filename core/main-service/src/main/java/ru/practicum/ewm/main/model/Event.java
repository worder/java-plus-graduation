package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "events")
public class Event {
    public enum EventState {
        PENDING,
        PUBLISHED,
        CANCELED
    }

    public enum UserStateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }

    public enum AdminStateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @Column(nullable = false, length = 7000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventState state = EventState.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "location_lat", nullable = false)
    private Double locationLat;

    @Column(name = "location_lon", nullable = false)
    private Double locationLon;

    @Column(nullable = false)
    private Boolean paid = false;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit = 0;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration = true;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "confirmed_requests", nullable = false)
    private Integer confirmedRequests = 0;

    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        if (state == null) {
            state = EventState.PENDING;
        }
    }
}