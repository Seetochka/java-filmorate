package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Director saveDirector(Director director) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlAddDirector = "INSERT INTO director (name) VALUES (?)";

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlAddDirector, new String[]{"id"});
                stmt.setString(1, director.getName());
                return stmt;
            }, keyHolder);
            director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        } catch (Exception e) {
            String message = "Не удалось добавить режиссёра и получить id";
            log.error("saveAndReturnId. {}", message);
            throw new RuntimeException(message);
        }
        return director;
    }

    public Director updateDirector(Director director) {
        String sqlUpdDirector = "UPDATE director SET name = ? WHERE id = ?";

        try {
            jdbcTemplate.update(sqlUpdDirector, director.getName(), director.getId());
            log.info("Данные режиссёра {} {} успешно обновлены", director.getId(), director.getName());
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить режиссёра";
            log.warn("updateDirector. {}", message);
            throw new RuntimeException(message);
        }

        return director;
    }

    public void deleteDirector(int id) {
        String sqlDeleteDirector = "DELETE FROM director WHERE id = ?";

        try {
            jdbcTemplate.update(sqlDeleteDirector, id);
            log.info("Режиссёр с id {} удалён", id);
        } catch (Exception e) {
            String message = "Не удалось удалить режиссёра";
            log.warn("deleteDirector. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Collection<Director> findAllDirectors() {
        String sqlAllDirectors = "SELECT id, name FROM director";
        try {
            return jdbcTemplate.query(sqlAllDirectors, (rs, rowNum) -> makeDirector(rs));
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список режиссёров";

            log.warn("findAllDirectors. {}", message);
            throw new RuntimeException(message);
        }
    }

    public Optional<Director> findDirectorById(int id) {
        String sqlDirectorById = "SELECT * FROM director WHERE id = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sqlDirectorById, (rs, rowNum) -> makeDirector(rs), id);
            return Optional.ofNullable(director);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            String message = "Не удалось получить режиссёра по указанному id";
            log.warn("getDirectorById. {}", message);
            throw new RuntimeException(message);
        }
    }

    public Collection<Director> findDirectorsByFilmId(int filmId) {
        String sqlDirectorByFilmId = "SELECT d.id, d.name " +
                "FROM director d " +
                "INNER JOIN film_director fd ON d.id = fd.director_id " +
                "WHERE fd.film_id = ?";
        try {
            return jdbcTemplate.query(sqlDirectorByFilmId, (rs, rowNum) -> makeDirector(rs), filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список режиссёров фильма";

            log.error("findDirectorByFilmId. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return Director.builder()
                .id(id)
                .name(name)
                .build();
    }
}
