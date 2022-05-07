package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

/**
 * Сервис пользователей
 */
@Service
@Slf4j
public class UserService {
    private final UserStorage storage;

    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    /**
     * Сохранение пользователя
     */
    public User saveUser(User user) {
        if (user.getName() == null || user.getName().trim().equals("")) {
            user.setName(user.getLogin());
        }

        return storage.saveUser(user);
    }

    /**
     * Получение пользователя
     */
    public User findById(int id) throws ModelNotFoundException {
        User user = storage.findById(id);

        if (user == null) {
            String message = String.format("Фильм с id %d не найден", id);

            log.warn("FindUserById. {}", message);
            throw new ModelNotFoundException(message);
        }

        return user;
    }

    /**
     * Получение всех пользователей
     */
    public Collection<User> findAll() {
        return storage.findAll();
    }

    /**
     * Обновление пользователя
     */
    public User updateUser(User user) throws ModelNotFoundException {
        findById(user.getId());

        return storage.updateUser(user);
    }

    /**
     * Сохранение в друзья
     */
    public int saveFriend(int userId, int friendId) throws ModelNotFoundException {
        findById(userId);
        findById(friendId);

        return storage.saveFriend(userId, friendId);
    }

    /**
     * Удаление из друзей
     */
    public int deleteFriend(int userId, int friendId) throws ModelNotFoundException {
        findById(userId);
        findById(friendId);

        return storage.deleteFriend(userId, friendId);
    }

    /**
     * Получение друзей пользователя
     */
    public Collection<User> findFriends(int userId) throws ModelNotFoundException {
        findById(userId);

        return storage.findFriends(userId);
    }

    /**
     * Получение друзей, общих с другим пользователем
     */
    public Collection<User> findCommonFriends(int userId, int otherUserId) throws ModelNotFoundException {
        findById(userId);
        findById(otherUserId);

        return storage.findCommonFriends(userId, otherUserId);
    }
}
