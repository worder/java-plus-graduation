package ru.practicum.ewm.main.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.practicum.ewm.main.error.exception.BadRequestException;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.NotFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format(
                        "'%s': %s (value: '%s')",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .toList();

        return new ApiErrorDto(
                HttpStatus.BAD_REQUEST.name(),
                "Validation error",
                String.join("; ", errors),
                LocalDateTime.now());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleBadRequest(BadRequestException e) {
        return new ApiErrorDto(
                HttpStatus.BAD_REQUEST.name(),
                "Bad request",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleMissingQueryParams(MissingServletRequestParameterException e) {
        return new ApiErrorDto(
                HttpStatus.BAD_REQUEST.name(),
                "Missing required query params",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return new ApiErrorDto(
                HttpStatus.BAD_REQUEST.name(),
                "Malformed JSON request or invalid parameter format",
                e.getMostSpecificCause().getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDto handleConflict(ConflictException e) {
        return new ApiErrorDto(
                HttpStatus.CONFLICT.name(),
                "Conflict exception",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDto handleNotFound(NotFoundException e) {
        return new ApiErrorDto(
                HttpStatus.NOT_FOUND.name(),
                "Resource not found",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDto handleNotFound(NoResourceFoundException e) {
        return new ApiErrorDto(
                HttpStatus.NOT_FOUND.name(),
                "Resource not found",
                e.getMessage(),
                LocalDateTime.now());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorDto internalError(Exception e) {
        log.error(e.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log.error(sw.toString());

        return new ApiErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Internal server error",
                e.getClass().toString(),
                LocalDateTime.now());
    }
}