package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

/**
 * Сервис фильмов
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;

    public FilmService(FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    /**
     * Сохранение фильма
     */
    public Film saveFilm(Film film) {
        return storage.saveFilm(film);
    }

    /**
     * Получение фильма
     */
    public Film findById(int id) throws ModelNotFoundException {
        Film film = storage.findById(id);

        if (film == null) {
            String message = String.format("Фильм с id %d не найден", id);

            log.warn("FindFilmById. {}", message);
            throw new ModelNotFoundException(message);
        }

        return film;
    }

    /**
     * Получение всех фильмов
     */
    public Collection<Film> findAll() {
        return storage.findAll();
    }

    /**
     * Обновление фильма
     */
    public Film updateFilm(Film film) throws ModelNotFoundException {
        findById(film.getId());

        return storage.updateFilm(film);
    }

    /**
     * Сохранение лайка
     */
    public int saveLike(int filmId, int userId) throws ModelNotFoundException {
        findById(filmId);
        userService.findById(userId);

        return storage.saveLike(filmId, userId);
    }

    /**
     * Удаление лайка
     */
    public int deleteLike(int filmId, int userId) throws ModelNotFoundException {
        findById(filmId);
        userService.findById(userId);

        return storage.deleteLike(filmId, userId);
    }

    /**
     * Получение переданного количества популярных фильмов
     */
    public Collection<Film> findPopularFilms(int count) throws IncorrectParameterException {
        if (count <= 0) {
            log.warn("FindPopular. Передан неверный параметр count {}", count);
            throw new IncorrectParameterException("count");
        }

        return storage.findPopularFilms(count);
    }
}
