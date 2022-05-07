package ru.yandex.practicum.filmorate.exceptions;

/**
 * Исключение неверного параметра
 */
public class IncorrectParameterException extends Exception {
    private final String parameter;

    public IncorrectParameterException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
