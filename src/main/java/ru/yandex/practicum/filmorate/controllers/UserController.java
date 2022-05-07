package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Контроллер для работы с пользователями
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody User user, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("SaveUser. " + message);
            throw new ValidationException(message);
        }

        User createdUser = service.saveUser(user);

        log.info(String.format("SaveUser. Пользователь с id %d успешно добавлен", user.getId()));
        return createdUser;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") int userId) throws ModelNotFoundException {
        return service.findById(userId);
    }

    @GetMapping
    public Collection<User> findAll() {
        return service.findAll();
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult)
            throws ValidationException, ModelNotFoundException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("UpdateUser. " + message);
            throw new ValidationException(message);
        }

        User updatedUser = service.updateUser(user);

        log.info(String.format("UpdateUser. Данные пользователя с id %d успешно обновлены", user.getId()));
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public int saveFriend(@PathVariable("id") int userId, @PathVariable int friendId) throws ModelNotFoundException {
        return service.saveFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public int deleteFriend(@PathVariable("id") int userId, @PathVariable int friendId) throws ModelNotFoundException {
        return service.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> gerFriends(@PathVariable("id") int userId) throws ModelNotFoundException {
        return service.findFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") int userId, @PathVariable("otherId") int otherUserId)
            throws ModelNotFoundException {
        return service.findCommonFriends(userId, otherUserId);
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .reduce("", (partialString, element) -> partialString + element);
    }
}
