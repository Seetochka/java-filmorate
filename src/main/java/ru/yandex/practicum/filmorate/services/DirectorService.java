package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.constants.Constant.LIKES;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;
    private final FilmDbStorage filmDbStorage;

    /**
     * Создание режиссёра
     */
    public Director saveDirector(Director director) {
        return directorDbStorage.saveDirector(director);
    }

    /**
     * Обновление данных режиссёра
     */
    public Director updateDirector(Director director) throws ModelNotFoundException {
        findDirectorById(director.getId());

        return directorDbStorage.updateDirector(director);
    }

    /**
     * Удаление режиссёра
     */
    public void deleteDirector(int id) throws ModelNotFoundException, ValidationException {
        findDirectorById(id);

        Collection<Film> filmsWithDirector = filmDbStorage.findFilmsByDirector(id, LIKES);

        List<Integer> filmsId = new ArrayList<>();
        filmsWithDirector.forEach(f -> filmsId.add(f.getId()));

        if (!filmsWithDirector.isEmpty()) {
            String message = "Режиссёра с id " + id + " удалить нельзя. Он указан в фильмах с id " + filmsId;

            log.error("DeleteDirector. {}", message);
            throw new ValidationException(message);
        }

        directorDbStorage.deleteDirector(id);
    }

    /**
     * Получение списка всех режиссёров
     */
    public Collection<Director> findAllDirectors() {
        return directorDbStorage.findAllDirectors();
    }

    /**
     * Получение режиссёра по Id
     */
    public Director findDirectorById(int id) throws ModelNotFoundException {
        Optional<Director> director = directorDbStorage.findDirectorById(id);

        director.orElseThrow(() -> {
            String message = String.format("Режиссёр с id %d не найден", id);

            log.warn("FindDirectorById. {}", message);
            return new ModelNotFoundException(message);
        });

        return director.get();
    }
}
