package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс хранилища фильмов
 */
public interface FilmStorage {
    /**
     * Сохранение фильма
     */
    Film saveFilm(Film film);

    /**
     * Удаление фильма
     */
    void deleteFilm(int id);

    /**
     * Получение фильма
     */
    Optional<Film> findById(int id);

    /**
     * Получение всех фильмов
     */
    Collection<Film> findAll();

    /**
     * Обновление фильма
     */
    Film updateFilm(Film film) throws ModelNotFoundException;

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
    Collection<Film> findPopularFilms(int count, Optional<Integer> genreId, Optional<Integer> year);


    /**
     * Поиск фильма по содержащейся строке в названии фильма
     */
    Collection<Film> findFilmsByTitleAndDirector(String query, String by);

    /**
     * Получение списка фильмов по id режиссёра
     */
    Collection<Film> findFilmsByDirector(long directorId, String sortBy);
}
