package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException exception) {
        log.error("Запрос на несуществующий ресурс. Ошибка: {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage(), "Ресурс не найден");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(ValidationException exception) {
        log.error("Ошибка валидации данных. Error: {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage(), "Ошибка валидации данных");
    }
}
