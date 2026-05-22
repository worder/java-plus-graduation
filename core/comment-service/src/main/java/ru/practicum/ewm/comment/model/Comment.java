package ru.practicum.ewm.comment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String text;

    private Long authorId;

    private Long eventId;

    private LocalDateTime createdOn;
    private LocalDateTime editedOn;
}
