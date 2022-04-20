package ru.yandex.practicum.filmorate.exceptions;

/**
 * Исключение отсутствия модели
 */
public class ModelNotFoundException extends RuntimeException {
    public ModelNotFoundException(String message) {
        super(message);
    }
}
