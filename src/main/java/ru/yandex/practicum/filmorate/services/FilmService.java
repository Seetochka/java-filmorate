package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.constants.Constant.*;


/**
 * Сервис фильмов
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;
    private final DirectorService directorService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage storage, UserService userService, DirectorService directorService) {
        this.storage = storage;
        this.userService = userService;
        this.directorService = directorService;
    }

    /**
     * Сохранение фильма
     */
    public Film saveFilm(Film film) throws ModelNotFoundException {

        if (film.getDirectors() != null) {
            // проверяем что у фильма указан id существующего режиссёра
            for (Director director : film.getDirectors()) {
                directorService.findDirectorById(director.getId());
            }
        }
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

        if (film.getDirectors() != null) {
            // проверяем что у фильма указан id существующего режиссёра
            for (Director director : film.getDirectors()) {
                directorService.findDirectorById(director.getId());
            }
        }

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

    /**
     * Поиск фильма по содержащейся строке в названии фильма или в имени режиссёра
     */
    public Collection<Film> searchFilmsByTitleAndDirector(String query, String by) throws IncorrectParameterException {
        if (by.equals(TITLE) || by.equals(DIRECTOR) ||
                by.equals(TITLE_AND_DIRECTOR) || by.equals(DIRECTOR_AND_TITLE)) {
            return storage.searchFilmsByTitleAndDirector(query, by);
        } else {
            log.warn("searchFilmsByTitleAndDirector. Передан неверный параметр by {}", by);
            throw new IncorrectParameterException("by");
        }
    }

    /**
     * Получение списка фильмов по id режиссёра
     */
    public Collection<Film> findFilmsByDirector(String directorId, String sortBy) throws IncorrectParameterException, ModelNotFoundException {

        try {
            Integer.parseInt(directorId);
        } catch (NumberFormatException e) {
            String message = "Введён неверный формат id";
            log.error("findFilmsByDirector. {}", message);
            throw new RuntimeException(message);
        }

        directorService.findDirectorById(Integer.parseInt(directorId));

        if (sortBy.equals(LIKES) || sortBy.equals(YEAR)) {
            return storage.findFilmsByDirector(Integer.parseInt(directorId), sortBy);
        } else {
            log.warn("findFilmsByDirector. Передан неверный параметр sortBy {}", sortBy);
            throw new IncorrectParameterException("sortBy");
        }

    }
}
