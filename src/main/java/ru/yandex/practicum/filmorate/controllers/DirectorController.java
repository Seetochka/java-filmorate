package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    /**
     * Создание режиссёра
     */
    @PostMapping
    public Director saveDirector(@Valid @RequestBody Director director, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("SaveDirector. {}", message);
            throw new ValidationException(message);
        }
        Director createdDirector = directorService.saveDirector(director);

        log.info("SaveDirector. Режиссёр с id {} успешно добавлен", createdDirector.getId());
        return createdDirector;
    }

    /**
     * Получение режиссёра по id
     */
    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable int id) throws ModelNotFoundException {
        return directorService.findDirectorById(id);
    }

    /**
     * Получение списка всех режиссёров
     */
    @GetMapping
    public Collection<Director> findAll() {
        return directorService.findAllDirectors();
    }

    /**
     * Обновление данных о режиссёре
     */
    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director, BindingResult bindingResult)
            throws ValidationException, ModelNotFoundException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);
            log.warn("UpdateDirector. " + message);
            throw new ValidationException(message);
        }
        Director updDirector = directorService.updateDirector(director);
        log.info("UpdateDirector. Режиссёр с id {} успешно обновлён", updDirector.getId());
        return updDirector;
    }

    /**
     * Удаление режиссёра
     */
    @DeleteMapping(value = {"/{id}"})
    @ResponseBody
    public void deleteDirector(@PathVariable int id) throws ModelNotFoundException, ValidationException {
        directorService.deleteDirector(id);

    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
