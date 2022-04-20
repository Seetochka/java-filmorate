package ru.yandex.practicum.filmorate.exceptions;

/**
 * Исключение валидации
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
