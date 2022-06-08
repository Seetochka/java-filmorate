package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    /**
     * Получение рейтинга
     */
    Optional<Mpa> findById(int id);

    /**
     * Получение всех рейтингов
     */
    Collection<Mpa> findAll();
}
