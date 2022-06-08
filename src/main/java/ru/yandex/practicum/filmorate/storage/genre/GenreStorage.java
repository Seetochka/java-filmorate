package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    /**
     * Получение жанра
     */
    Optional<Genre> findById(int id);

    /**
     * Получение всех жанров
     */
    Collection<Genre> findAll();
}
