package ru.yandex.practicum.filmorate.storage.likeReview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class LikeReviewDbStorage implements LikeReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void put(int reviewId, int userId, boolean isLike) {
        String sqlQuery = "INSERT INTO like_review (review_id, user_id, is_like) VALUES (?, ?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, reviewId, userId, isLike);
        } catch (Exception e) {
            String message = "Не удалось добавить запись";

            log.error("Put. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void delete(int reviewId, int userId) {
        String sqlQuery = "DELETE FROM like_review WHERE review_id = ? AND user_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, reviewId, userId);
        } catch (Exception e) {
            String message = "Не удалось удалить запись";

            log.error("Delete. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Optional<Boolean> getStatus(int reviewId, int userId) {

        String sqlQuery = "SELECT is_like FROM like_review WHERE review_id = ? AND user_id = ?";

        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, reviewId, userId);
            if(rs.next()) {
                return Optional.of(rs.getBoolean("is_like"));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            String message = "Не удалось получить запись";

            log.error("Check. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void update(int reviewId, int userId, boolean isLike) {
        String sqlQuery = "UPDATE like_review SET is_like = ? WHERE review_id = ? AND user_id = ?";

        try {
            jdbcTemplate.update(sqlQuery, isLike, reviewId, userId);
        } catch (Exception e) {
            String message = "Не удалось обновить запись";

            log.error("Update. {}", message);
            throw new RuntimeException(message);
        }
    }

}
