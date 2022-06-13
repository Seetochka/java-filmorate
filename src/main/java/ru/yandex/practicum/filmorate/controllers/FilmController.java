package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

/**
 * Контроллер для работы с фильмами
*/
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;

    @PostMapping
    public Film saveFilm(@Valid @RequestBody Film film, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("SaveFilm. {}", message);
            throw new ValidationException(message);
        }

        Film createdFilm = service.saveFilm(film);

        log.info("SaveFilm. Фильм с id {} успешно добавлен", film.getId());
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

        log.info("UpdateFilm. Фильм с id {} успешно обновлен", film.getId());
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public int saveLike(@PathVariable("id") int filmId, @PathVariable int userId) throws ModelNotFoundException {
        int countLikes = service.saveLike(filmId, userId);

        log.info("SaveLike. Пользователь с id {} добавил лайк фильму с id {}", userId, filmId);
        return countLikes;
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") int filmId) throws ModelNotFoundException {
        service.deleteFilm(filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public int deleteLike(@PathVariable("id") int filmId, @PathVariable int userId) throws ModelNotFoundException {
        int countLikes = service.deleteLike(filmId, userId);

        log.info("DeleteLike. Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
        return countLikes;
    }

    @GetMapping("/popular")
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                             @RequestParam(required = false) Optional<Integer> year,
                                             @RequestParam(required = false) Optional<Integer> genreId)
            throws IncorrectParameterException {
        return service.findPopularFilms(count, genreId, year);
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .reduce("", (partialString, element) -> partialString + element);
    }
}
