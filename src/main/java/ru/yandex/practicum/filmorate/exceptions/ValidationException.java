package ru.yandex.practicum.filmorate.exceptions;

/**
 * Исключение валидации
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
