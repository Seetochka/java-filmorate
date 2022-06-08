package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.services.GenreService;

import java.util.Collection;

/**
 * Контроллер для работы с жанрами
 */
@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final GenreService service;

    @GetMapping("/{id}")
    public Genre findById(@PathVariable("id") int genreId) throws ModelNotFoundException {
        return service.findById(genreId);
    }

    @GetMapping
    public Collection<Genre> findAll() {
        return service.findAll();
    }
}
