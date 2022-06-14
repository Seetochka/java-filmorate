package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Хранилище фильмов
 */
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static int globalId = 1;

    private Map<Integer, Film> films = new ConcurrentHashMap<>();

    /**
     * Сохранение фильма
     */
    @Override
    public Film saveFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public void deleteFilm(int id) {
        films.remove(id);
    }

    /**
     * Получение фильма
     */
    @Override
    public Optional<Film> findById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    /**
     * Получение всех фильмов
     */
    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    /**
     * Обновление фильма
     */
    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);

        return film;
    }

    /**
     * Сохранение лайка
     */
    @Override
    public int saveLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.saveLike(userId);

        return film.getCountLikes();
    }

    /**
     * Удаление лайка
     */
    @Override
    public int deleteLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.deleteLike(userId);

        return film.getCountLikes();
    }

    /**
     * Получение переданного количества популярных фильмов
     */
    @Override
    public Collection<Film> findPopularFilms(int count, Optional<Integer> genreId, Optional<Integer> year) {
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(Film::getCountLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> findFilmsByTitleAndDirector(String query, String by) {
        return null;
    }

    @Override
    public Collection<Film> findFilmsByDirector(long directorId, String sortBy) {
        return null;
    }

    @Override
    public Collection<Film> findCommonFilms(int userId, int friendId) {
        return null;
    }

    private static Integer getNextId() {
        return globalId++;
    }
}
