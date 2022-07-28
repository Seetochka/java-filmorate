package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получение жанра
     */
    @Override
    public Optional<Genre> findById(int id) {
        String sqlQuery = "SELECT id, name FROM genre WHERE id = ?";

        try {
            Genre genre = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);

            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            String message = "Не удалось получить жанр";

            log.error("FindGenreById. {}", message);
            throw new RuntimeException(message);
        }
    }

    /**
     * Получение всех жанров
     */
    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT id, name FROM genre";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список жанров";

            log.error("FindAllGenre. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
