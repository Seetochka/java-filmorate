package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Контроллер для работы с фильмами
*/
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping
    public Film saveFilm(@Valid @RequestBody Film film, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("SaveFilm. " + message);
            throw new ValidationException(message);
        }

        Film createdFilm = service.saveFilm(film);

        log.info(String.format("SaveFilm. Фильм с id %d успешно добавлен", film.getId()));
        return createdFilm;
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") int filmId) throws ModelNotFoundException {
        return service.findById(filmId);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return service.findAll();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult)
            throws ValidationException, ModelNotFoundException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("UpdateFilm. " + message);
            throw new ValidationException(message);
        }

        Film updatedFilm = service.updateFilm(film);

        log.info(String.format("UpdateFilm. Фильм с id %d успешно обновлен", film.getId()));
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public int saveLike(@PathVariable("id") int filmId, @PathVariable int userId) throws ModelNotFoundException {
        int countLikes = service.saveLike(filmId, userId);

        log.info(String.format("SaveLike. Пользователь с id %d добавил лайк фильму с id %d", userId, filmId));
        return countLikes;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public int deleteLike(@PathVariable("id") int filmId, @PathVariable int userId) throws ModelNotFoundException {
        int countLikes = service.deleteLike(filmId, userId);

        log.info(String.format("DeleteLike. Пользователь с id %d удалил лайк у фильма с id %d", userId, filmId));
        return countLikes;
    }

    @GetMapping("/popular")
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count)
            throws IncorrectParameterException {
        if (count <= 0) {
            log.warn(String.format("FindPopular. Передан неверный параметр count %d", count));
            throw new IncorrectParameterException("count");
        }

        return service.findPopularFilms(count);
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .reduce("", (partialString, element) -> partialString + element);
    }
}
