package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.event.EventDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис фильмов
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;
    private final EventDBStorage eventStorage;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage storage,
                       UserService userService,
                       @Qualifier("EventDBStorage") EventDBStorage eventStorage) {
        this.storage = storage;
        this.userService = userService;
        this.eventStorage = eventStorage;
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
        Optional<Film> film = storage.findById(id);

        film.orElseThrow(() -> {
            String message = String.format("Фильм с id %d не найден", id);

            log.warn("FindFilmById. {}", message);
            return new ModelNotFoundException(message);
        });

        return film.get();
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
        int ret = storage.saveLike(filmId, userId);
        eventStorage.log(userId, "LIKE", "ADD", filmId);
        return ret;
    }

    /**
     * Удаление лайка
     */
    public int deleteLike(int filmId, int userId) throws ModelNotFoundException {
        findById(filmId);
        userService.findById(userId);
        int ret = storage.deleteLike(filmId, userId);
        eventStorage.log(userId, "LIKE", "REMOVE", filmId);
        return ret;
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
