package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.storage.LikeReview.LikeReviewStorage;
import ru.yandex.practicum.filmorate.storage.rewie.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис для работы с отзывами
 */

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage storage;
    private final FilmService filmService;
    private final LikeReviewStorage likeReviewStorage;

    private final UserService userService;

    public ReviewService(ReviewStorage storage, FilmService filmService,
                         LikeReviewStorage likeReviewStorage, UserService userService) {

        this.storage = storage;
        this.filmService = filmService;
        this.likeReviewStorage = likeReviewStorage;
        this.userService = userService;
    }

    /**
     * Сохранение отзыва
     */
    public Review saveReview(Review review) {
        return storage.saveReview(review);
    }

    /**
     * Получение отзыва
     */
    public Review findById(int id) throws ModelNotFoundException {
        Optional<Review> review = storage.findById(id);

        return review.orElseThrow(() -> {
            String message = String.format("Отзыв с id %d не найден", id);

            log.warn("FindReviewById. {}", message);
            return new ModelNotFoundException(message);
        });
    }

    /**
     * Получение всех отзывов определённого фильма
     */
    public Collection<Review> findByFilmId(Optional<Integer> id, int count) throws ModelNotFoundException, IncorrectParameterException {

        if (count <= 0) {
            log.warn("FindByFilmId. Передан неверный параметр count {}", count);
            throw new IncorrectParameterException("count");
        }

        if (id.isPresent()) {
            filmService.findById(id.get());
            return storage.findByFilmId(id.get(), count);
        }

        return storage.findAll(count);
    }

    /**
     * Обновление отзыва
     */
    public Review updateReview(Review review) throws ModelNotFoundException {
        findById(review.getReviewId());

        return storage.updateReview(review);
    }

    /**
     * Удаление отзыва
     */
    public void deleteFilm(int id) throws ModelNotFoundException {
        findById(id);

        storage.deleteReview(id);
    }

    /**
     * Поставить лайк отзыву
     */
    public void putLikeReview(int reviewId, int userId) throws ModelNotFoundException {
        Optional<Boolean> status = likeReviewStorage.getStatus(reviewId, userId);

        Review review = findById(reviewId);
        userService.findById(userId);

        if (status.isPresent()) {
            if(!status.get()) {
                likeReviewStorage.update(reviewId, userId, true);
                review.setUseful(review.getUseful() + 2);
            }
        } else {
            likeReviewStorage.put(reviewId, userId, true);
            review.setUseful(review.getUseful() + 1);
        }
        storage.updateReview(review);
    }

    /**
     * Поставить дизлайк отзыву
     */
    public void putDislikeReview(int reviewId, int userId) throws ModelNotFoundException {
        Review review = findById(reviewId);
        userService.findById(userId);

        Optional<Boolean> status = likeReviewStorage.getStatus(reviewId, userId);
        if (status.isPresent()) {
            if(status.get()) {
                likeReviewStorage.update(reviewId, userId, false);
                review.setUseful(review.getUseful() - 2);
            }
        } else {
            likeReviewStorage.put(reviewId, userId, false);
            review.setUseful(review.getUseful() - 1);
        }
        storage.updateReview(review);
    }

    /**
     * Удалить лайк отзыву
     */
    public void deleteLikeReview(int reviewId, int userId) throws ModelNotFoundException {
        Review review = findById(reviewId);
        userService.findById(userId);

        Optional<Boolean> status = likeReviewStorage.getStatus(reviewId, userId);
        if (status.isPresent()) {
            if(status.get()) {
                likeReviewStorage.delete(reviewId, userId);
                review.setUseful(review.getUseful() - 1);
            }
        } else {
            String message = "Лайк не существует";

            log.error("DeleteLikeReview. {}", message);
            throw new ModelNotFoundException(message);
        }
        storage.updateReview(review);
    }

    /**
     * Удалить дизлайк отзыву
     */
    public void deleteDislikeReview(int reviewId, int userId) throws ModelNotFoundException {
        Review review = findById(reviewId);
        userService.findById(userId);

        Optional<Boolean> status = likeReviewStorage.getStatus(reviewId, userId);
        if (status.isPresent()) {
            if(!status.get()) {
                likeReviewStorage.delete(reviewId, userId);
                review.setUseful(review.getUseful() + 1);
            }
        } else {
            String message = "Дизлайк не существует";
            log.error("DeleteLikeReview. {}", message);
            throw new ModelNotFoundException(message);
        }
        storage.updateReview(review);
    }
}

