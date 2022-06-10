package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Сервис фильмов
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage storage, UserService userService) {
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

    // поиск фильма по содержащейся строке в названии фильма
    public Collection<Film> searchFilmsByTitle(String query, String by) throws IncorrectParameterException {
        if (by.equals("title") || by.equals("director") || by.equals("title,director") || by.equals("director,title")) {
            return storage.searchFilmsByTitle(query, by);
        } else {
            log.warn("searchFilmsByTitle. Передан неверный параметр by {}", by);
            throw new IncorrectParameterException("by");
        }
    }

    // получение списка фильмов по id режиссёра
    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) throws IncorrectParameterException {
        if (sortBy.equals("likes") || sortBy.equals("year")) {
            return storage.getFilmsByDirector(directorId, sortBy);
        } else {
            log.warn("getFilmsByDirector. Передан неверный параметр sortBy {}", sortBy);
            throw new IncorrectParameterException("sortBy");
        }

    }
}
