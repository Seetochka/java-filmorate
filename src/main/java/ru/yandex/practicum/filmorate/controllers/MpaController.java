package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.services.MpaService;

import java.util.Collection;

/**
 * Контроллер для работы с рейтингами
 */
@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaService service;

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable("id") int mpaId) throws ModelNotFoundException {
        return service.findById(mpaId);
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        return service.findAll();
    }
}
