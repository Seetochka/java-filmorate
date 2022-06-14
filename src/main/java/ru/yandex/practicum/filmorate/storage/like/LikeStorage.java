package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.models.Like;

import java.util.Collection;

public interface LikeStorage {
    /**
     * Получение лайков пользователя
     */
    Collection<Like> findLikeFilmsByUserId(int userId);

    /**
     * Получение всех лайков
     */
    Collection<Like> findLikes();
}
