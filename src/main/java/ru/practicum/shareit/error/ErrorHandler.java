package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.NoEnumValueArgumentException;
import ru.practicum.shareit.exception.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidEmailException(final ValidationException e) {
        log.info("Ошибка валидации");
        return new ErrorResponse(
                e.getMessage(), e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidEmailException(final DataNotFoundException e) {
        log.info("Ошибка валидации");
        return new ErrorResponse(
                "Ошибка валидации", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidUserNotFound(final IllegalArgumentException e) {
        log.info("Ошибка валидации");
        return new ErrorResponse(
                "Ошибка валидации", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoEnumValueArgumentException(final NoEnumValueArgumentException e) {
        log.error("Unknown state: UNSUPPORTED_STATUS, {}", e.getMessage());
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }
}
