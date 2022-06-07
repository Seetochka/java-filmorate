package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис пользователей
 */
@Service
@Slf4j
public class UserService {
    private final UserStorage storage;

    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
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
        Optional<User> user = storage.findById(id);

        user.orElseThrow(() -> {
            String message = String.format("Пользователь с id %d не найден", id);

            log.warn("FindUserById. {}", message);
            return new ModelNotFoundException(message);
        });

        return user.get();
    }

    /**
     * Удаление пользователя
     */
    public void deleteUser(int id) throws ModelNotFoundException {
        findById(id);

        storage.deleteUser(id);
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
