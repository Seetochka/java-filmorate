package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис жанров
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage storage;

    /**
     * Получение жанра
     */
    public Genre findById(int id) throws ModelNotFoundException {
        Optional<Genre> genre = storage.findById(id);

        genre.orElseThrow(() -> {
            String message = String.format("Данр с id %d не найден", id);

            log.warn("FindGenreById. {}", message);
            return new ModelNotFoundException(message);
        });

        return genre.get();
    }

    /**
     * Получение всех жанров
     */
    public Collection<Genre> findAll() {
        return storage.findAll();
    }
}
