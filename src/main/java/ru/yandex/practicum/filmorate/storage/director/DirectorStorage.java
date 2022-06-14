package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.models.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {

    /**
     * Создание режиссёра
     */
    Director saveDirector(Director director);

    /**
     * Обновление данных режиссёра
     */
    Director updateDirector(Director director);

    /**
     * Удаление режиссёра
     */
    String deleteDirector(int id);

    /**
     * Получение списка всех режиссёров
     */
    Collection<Director> findAllDirectors();

    /**
     * Получение режиссёра по Id
     */
    Optional<Director> findDirectorById(int id);

    /**
     * Получение режиссёра по id фильма
     */
    Collection<Director> findDirectorsByFilmId(int filmId);
}
