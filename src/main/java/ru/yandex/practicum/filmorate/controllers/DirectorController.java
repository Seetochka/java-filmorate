package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {
    DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    // создание режиссёра
    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);
            log.warn("saveDirector. {}", message);
            throw new ValidationException(message);
        }
        Director createdDirector = directorService.createDirector(director);
        log.info("createDirector. Режиссёр с id {} успешно добавлен", createdDirector.getId());
        return createdDirector;
    }

    // получение режиссёра по id
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable String id) throws ModelNotFoundException {
        try {
            return directorService.getDirectorById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            String message = "Не удалось получить режиссёра";
            log.warn("getDirectorById. {}", message);
            throw new RuntimeException(message);
        }
    }

    // получение списка всех режиссёров
    @GetMapping
    public List<Director> findAll() {
        return directorService.getAllDirectors();
    }

    // обновление данных о режиссёре
    @PutMapping(value = {"/{id}"})
    public Director updateDirector(@Valid @RequestBody Director director, @PathVariable(required = false) String id, BindingResult bindingResult)
            throws ValidationException, IncorrectParameterException {
        try {
            if (Long.parseLong(id) != director.getId()) {
                String message = "id в теле запроса и в URL разный";
                log.warn("getDirectorById. {}", message);
                throw new IncorrectParameterException(message);
            }
        } catch (NumberFormatException e) {
            String message = "Не удалось получить режиссёра по id";
            log.warn("getDirectorById. {}", message);
            throw new RuntimeException(message);
        }
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);
            log.warn("updateDirector. " + message);
            throw new ValidationException(message);
        }
        Director updDirector = directorService.updateDirector(director);
        log.info("updateDirector. Режиссёр с id {} успешно обновлён", updDirector.getId());
        return updDirector;
    }

    // удаление режиссёра
    @DeleteMapping(value = {"/{id}"})
    @ResponseBody
    public String removeDirector(@PathVariable(required = false) String id) {
        try {
            return directorService.removeDirector(Long.parseLong(id));
        } catch (NumberFormatException e) {
            String message = "Не удалось удалить режиссёра";
            log.warn("removeDirector. {}", message);
            throw new RuntimeException(message);
        }
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .reduce("", (partialString, element) -> partialString + element);
    }
}
