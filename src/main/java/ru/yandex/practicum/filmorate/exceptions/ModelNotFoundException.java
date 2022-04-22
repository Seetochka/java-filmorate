package ru.yandex.practicum.filmorate.exceptions;

/**
 * Исключение отсутствия модели
 */
public class ModelNotFoundException extends Exception {
    public ModelNotFoundException(String message) {
        super(message);
    }
}
