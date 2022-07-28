package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review saveReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO review (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, review.getContent());
                stmt.setBoolean(2, review.getIsPositive());
                stmt.setInt(3, review.getUserId());
                stmt.setInt(4, review.getFilmId());
                stmt.setInt(5, 0);
                return stmt;
            }, keyHolder);

            review.setId(
                    Objects.requireNonNull(keyHolder.getKey()).intValue()
            );
            review.setUseful(0);
        } catch (Exception e) {
            String message = "Не удалось сохранить отзыв";

            log.error("SaveReview. {}", message);
            throw new RuntimeException(message);
        }

        return review;
    }

    @Override
    public Optional<Review> findById(int id) {
        String sqlQuery = "SELECT id, content, is_positive, user_id, film_id, useful FROM review WHERE id = ?";

        try {
            Review review = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);

            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            String message = "Не удалось получить отзыв";

            log.warn("FindReviewById. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE review SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ? " +
                "WHERE id = ?";

        try {
            jdbcTemplate.update(sqlQuery,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getUserId(),
                    review.getFilmId(),
                    review.getUseful(),
                    review.getId());
        } catch (Exception e) {
            String message = "Не удалось обновить данные обзора";

            log.error("UpdateReview. {}", message);
            throw new RuntimeException(message);
        }

        return review;
    }

    @Override
    public Collection<Review> findByFilmId(int id, int count) {
        String sqlQuery = "SELECT id, content, is_positive, user_id, film_id, useful FROM review WHERE film_id = ?" +
                "ORDER BY useful DESC, id LIMIT ?";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, id, count);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список фильмов";

            log.error("FindAllFilms. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Collection<Review> findAll(int count) {
        String sqlQuery = "SELECT id, content, is_positive, user_id, film_id, useful FROM review " +
                "ORDER BY useful DESC LIMIT ?";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список фильмов";

            log.error("FindAllFilms. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void deleteReview(int id) {
        String sqlQuery = "DELETE FROM review WHERE id = ?";

        try {
            jdbcTemplate.update(sqlQuery, id);
        } catch (Exception e) {
            String message = "Не удалось удалить отзыв";

            log.error("DeleteReview. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Review mapRowToReview(ResultSet resultSet, int i) throws SQLException {
        return Review.builder()
                .id(resultSet.getInt("id"))
                .content(resultSet.getString("content"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}
