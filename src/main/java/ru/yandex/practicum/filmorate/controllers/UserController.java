package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Контроллер для работы с пользователями
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Integer, User> users = new ConcurrentHashMap<>();

    @PostMapping
    public String create(@Valid @RequestBody User user, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String message = String.format("Ошибки валидации: %s", getStringErrors(bindingResult));

            log.warn(message);
            throw new ValidationException(message);
        }

        if (user.getName() == null || user.getName().trim().equals("")) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);

        String message = String.format("Пользователь %s успешно добавлен", user.getLogin());
        log.info(message);
        return String.format(message);
    }

    @PutMapping
    public String update(@Valid @RequestBody User user, BindingResult bindingResult)
            throws ValidationException, ModelNotFoundException {
        if (bindingResult.hasErrors()) {
            String message = String.format("Ошибки валидации: %s", getStringErrors(bindingResult));

            log.warn(message);
            throw new ValidationException(message);
        }

        if (!users.containsKey(user.getId())) {
            String message = String.format("Не найден пользователь с логином %s", user.getLogin());

            log.warn(message);
            throw new ModelNotFoundException(message);
        }

        users.put(user.getId(), user);

        String message = String.format("Данные пользователя %s успешно обновлены", user.getLogin());
        log.info(message);
        return String.format(message);
    }

    @GetMapping
    public Map<Integer, User> getUsers() {
        return users;
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .reduce("", (partialString, element) -> partialString + element);
    }
}
