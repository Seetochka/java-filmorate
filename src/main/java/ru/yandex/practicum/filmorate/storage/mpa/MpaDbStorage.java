package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получение рейтинга
     */
    @Override
    public Optional<Mpa> findById(int id) {
        String sqlQuery = "SELECT id, name FROM mpa WHERE id = ?";

        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);

            return Optional.ofNullable(mpa);
        }  catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            String message = "Не удалось получить рейтинг";

            log.error("FindMpaById. {}", message);
            throw new RuntimeException(message);
        }
    }

    /**
     * Получение всех рейтингов
     */
    @Override
    public Collection<Mpa> findAll() {
        String sqlQuery = "SELECT id, name FROM mpa";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список рейтингов";

            log.error("FindAllMpa. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int i) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
