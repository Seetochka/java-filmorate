package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public String create(@Valid @RequestBody Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = String.format("Ошибки валидации: %s", getStringErrors(bindingResult));

            log.warn(message);
            throw new ValidationException(message);
        }

        films.put(film.getId(), film);

        String message = String.format("Фильм %s успешно добавлен", film.getName());
        log.info(message);
        return String.format(message);
    }

    @PutMapping
    public String update(@Valid @RequestBody Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = String.format("Ошибки валидации: %s", getStringErrors(bindingResult));

            log.warn(message);
            throw new ValidationException(message);
        }

        if (!films.containsKey(film.getId())) {
            String message = String.format("Не найден фильм с названием %s", film.getName());

            log.warn(message);
            throw new ModelNotFoundException(message);
        }

        films.put(film.getId(), film);

        String message = String.format("Фильм %s успешно обновлен", film.getName());
        log.info(message);
        return String.format(message);
    }

    @GetMapping
    public Map<Integer, Film> getFilms() {
        return films;
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .reduce("", (partialString, element) -> partialString + element);
    }
}
