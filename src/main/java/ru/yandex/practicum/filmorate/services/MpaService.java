package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис категорий
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage storage;

    /**
     * Получение категории
     */
    public Mpa findById(int id) throws ModelNotFoundException {
        Optional<Mpa> mpa = storage.findById(id);

        mpa.orElseThrow(() -> {
            String message = String.format("Рейтинг с id %d не найден", id);

            log.warn("FindMpaById. {}", message);
            return new ModelNotFoundException(message);
        });

        return mpa.get();
    }

    /**
     * Получение всех категорий
     */
    public Collection<Mpa> findAll() {
        return storage.findAll();
    }
}
