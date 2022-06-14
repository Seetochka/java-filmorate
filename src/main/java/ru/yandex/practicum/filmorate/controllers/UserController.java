package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.RecommendationService;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Контроллер для работы с пользователями
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;

    @PostMapping
    public User saveUser(@Valid @RequestBody User user, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("SaveUser. {}", message);
            throw new ValidationException(message);
        }

        User createdUser = userService.saveUser(user);

        log.info("SaveUser. Пользователь с id {} успешно добавлен", user.getId());
        return createdUser;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") int userId) throws ModelNotFoundException {
        return userService.findById(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int userId) throws ModelNotFoundException {
        service.deleteUser(userId);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult)
            throws ValidationException, ModelNotFoundException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("UpdateUser. {}", message);
            throw new ValidationException(message);
        }

        User updatedUser = userService.updateUser(user);

        log.info("UpdateUser. Данные пользователя с id {} успешно обновлены", user.getId());
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public int saveFriend(@PathVariable("id") int userId, @PathVariable int friendId) throws ModelNotFoundException {
        int countFriends = userService.saveFriend(userId, friendId);

        log.info("SaveFriend. Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
        return countFriends;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public int deleteFriend(@PathVariable("id") int userId, @PathVariable int friendId) throws ModelNotFoundException {
        int countFriends = userService.deleteFriend(userId, friendId);

        log.info("DeleteFriend. Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
        return countFriends;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> gerFriends(@PathVariable("id") int userId) throws ModelNotFoundException {
        return userService.findFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") int userId, @PathVariable("otherId") int otherUserId)
            throws ModelNotFoundException {
        return userService.findCommonFriends(userId, otherUserId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable("id") int userId) {
        return recommendationService.findRecommendations(userId);
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .reduce("", (partialString, element) -> partialString + element);
    }
}
