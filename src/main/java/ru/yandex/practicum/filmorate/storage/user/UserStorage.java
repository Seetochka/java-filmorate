package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.Collection;

/**
 * Интерфейс хранилища пользователей
 */
public interface UserStorage {
    /**
     * Сохранение пользователя
     */
    User saveUser(User user);

    /**
     * Получение пользователя
     */
    User findById(int id);

    /**
     * Получение всех пользователей
     */
    Collection<User> findAll();

    /**
     * Обновление пользователя
     */
    User updateUser(User user);

    /**
     * Сохранение в друзья
     */
    int saveFriend(int userId, int friendId);

    /**
     * Удаление из друзей
     */
    int deleteFriend(int userId, int friendId);

    /**
     * Получение друзей пользователя
     */
    Collection<User> findFriends(int userId);

    /**
     * Получение друзей, общих с другим пользователем
     */
    Collection<User> findCommonFriends(int userId, int otherUserId);
}
