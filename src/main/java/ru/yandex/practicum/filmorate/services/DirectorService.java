package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class DirectorService {

    DirectorDbStorage directorDbStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    // создание режиссёра
    public Director createDirector(Director director) {
        return directorDbStorage.createDirector(director);
    }

    // обновление данных режиссёра
    public Director updateDirector(Director director) throws ValidationException {
       return directorDbStorage.updateDirector(director);
    }

    // удаление режиссёра
    public String removeDirector(Long id) {
        return directorDbStorage.removeDirector(id);
    }

    // получение списка всех режиссёров
    public Collection<Director> getAllDirectors() {
       return directorDbStorage.getAllDirectors();

    }

    // получение режиссёра по Id
    public Director getDirectorById(Long id) throws ModelNotFoundException {

        Optional<Director> director = directorDbStorage.getDirectorById(id);

        director.orElseThrow(() -> {
            String message = String.format("Режиссёр с id %d не найден", id);

            log.warn("getDirectorById. {}", message);
            return new ModelNotFoundException(message);
        });
        return director.get();

    }

}
