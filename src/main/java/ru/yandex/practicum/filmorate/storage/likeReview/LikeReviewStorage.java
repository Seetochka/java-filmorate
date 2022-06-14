package ru.yandex.practicum.filmorate.storage.likeReview;

import java.util.Optional;

public interface LikeReviewStorage {
    /**
     * Поставить дизлайк
     */
    void put(int reviewId, int userId, boolean isLike);

    /**
     * Удалить лайк и дизлайк
     */
    void delete(int reviewId, int userId);

    /**
     * Проверить есть ли запись
     */
    Optional<Boolean> getStatus(int reviewId, int userId);

    /**
     * Обновить запись
     */
    void update(int reviewId, int userId, boolean isLike);
}
