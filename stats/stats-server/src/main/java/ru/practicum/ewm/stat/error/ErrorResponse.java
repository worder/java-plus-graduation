package ru.practicum.ewm.stat.error;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;
    String details;
}
