package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.models.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    /**
     * Сохранение отзыва
     */
    Review saveReview(Review review);

    /**
     * Получение отзыва по id
     */
    Optional<Review> findById(int id);

    /**
     * Обновление отзыва
     */
    Review updateReview(Review review);

    /**
     * Получение отзыва по id фильма
     */
    Collection<Review> findByFilmId(int id, int count);

    /**
     * Получение всех отзывов
     */
    Collection<Review> findAll(int count);

    /**
     * Удаление отзыва
     */
    void deleteReview(int id);
}
