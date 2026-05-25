package ru.practicum.ewm.user.error;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.interaction.error.ErrorHandler;

@RestControllerAdvice
public class LocalErrorHandler extends ErrorHandler {
}
