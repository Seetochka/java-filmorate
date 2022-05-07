package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.Collection;

/**
 * Интерфейс хранилища фильмов
 */
public interface FilmStorage {
    /**
     * Сохранение фильма
     */
    Film saveFilm(Film film);

    /**
     * Получение фильма
     */
    Film findById(int id);

    /**
     * Получение всех фильмов
     */
    Collection<Film> findAll();

    /**
     * Обновление фильма
     */
    Film updateFilm(Film film);

    /**
     * Сохранение лайка
     */
    int saveLike(int filmId, int userId);

    /**
     * Удаление лайка
     */
    int deleteLike(int filmId, int userId);

    /**
     * Получение переданного количества популярных фильмов
     */
    Collection<Film> findPopularFilms(int count);
}
