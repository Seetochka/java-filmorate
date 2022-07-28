package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получение лайков пользователя
     */
    public Collection<Like> findLikeFilmsByUserId(int userId) {
        String sqlQuery = "SELECT user_id, film_id FROM `like` WHERE user_id = ?";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToLike, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список лайков пользователя";

            log.error("FindLikeFilmsByUserId. {}", message);
            throw new RuntimeException(message);
        }
    }

    /**
     * Получение всех лайков
     */
    public Collection<Like> findLikes() {
        String sqlQuery = "SELECT user_id, film_id FROM `like`";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToLike);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить все лайки";

            log.error("FindLikes. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Like mapRowToLike(ResultSet resultSet, int i) throws SQLException {
        return Like.builder()
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .build();
    }
}
