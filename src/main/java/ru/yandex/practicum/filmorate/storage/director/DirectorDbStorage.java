package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class DirectorDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // создание режиссёра
    public Director createDirector(Director director) {
        long id = saveAndReturnId(director); // добавлили режиссёра в таблицу и получили id
        return Director.builder()
                .id(id)
                .name(director.getName())
                .build();
    }

    //добавляет режиссёра в таблицу и возвращает Id
    private long saveAndReturnId(Director director) {
        String sqlAddDirector = "INSERT INTO director (name) VALUES (?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlAddDirector, new String[]{"id"});
                stmt.setString(1, director.getName());
                return stmt;
            }, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (Exception e) {
            String message = "Не удалось добавить фильм и получить id";
            log.error("saveAndReturnId. {}", message);
            throw new RuntimeException(message);
        }
    }

    // обновление данных режиссёра
    public Director updateDirector(Director director) throws ValidationException {
        String sqlUpdDirector = "UPDATE director SET name = ? WHERE id = ?";
        String sqlDirectorById = "SELECT * FROM director WHERE id = ?";
        SqlRowSet userDirector = jdbcTemplate.queryForRowSet(sqlDirectorById, director.getId());
        if (userDirector.next()) {
            jdbcTemplate.update(sqlUpdDirector, director.getName(), director.getId());
            log.info("Данные режиссёра {} успешно обновлены", director.getName());
            return director;
        } else {
            log.warn("Введён неверный id");
            throw new ValidationException(String.format("Режиссёр с id %d не найден", director.getId()));
        }
    }


    // удаление режиссёра
    public String removeDirector(Long id) {
        String sqlDeleteDirector = "DELETE FROM director WHERE id = ?";
        if (jdbcTemplate.update(sqlDeleteDirector, id) == 0) {
            String message = "Не удалось удалить данные режиссёра";
            log.error("removeDirector. {}", message);
            throw new RuntimeException(message);
        }
        return String.format("Режиссёр с id %d удалён", id);
    }

    // получение списка всех режиссёров
    public Collection<Director> getAllDirectors() {
        String sqlAllDirectors = "SELECT * FROM director";
        try {
            return jdbcTemplate.query(sqlAllDirectors, (rs, rowNum) -> makeDirector(rs, jdbcTemplate));
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить режиссёра";
            log.warn("getDirectorById. {}", message);
            throw new RuntimeException(message);
        }
    }

    // получение режиссёра по Id
    public Optional<Director> getDirectorById(Long id) {
        String sqlDirectorById = "SELECT * FROM director WHERE id = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sqlDirectorById, (rs, rowNum) -> makeDirector(rs, jdbcTemplate), id);
            return Optional.ofNullable(director);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            String message = "Не удалось получить режиссёра";
            log.warn("getDirectorById. {}", message);
            throw new RuntimeException(message);
        }
    }

    public Collection<Director> findDirectorByFilmId(int filmId) {
        String sqlDirectorByFilmId = "SELECT d.id, d.name " +
                "FROM director d " +
                "INNER JOIN film_director fd ON d.id = fd.director_id " +
                "WHERE fd.film_id = ?";
        try {
            return jdbcTemplate.query(sqlDirectorByFilmId, (rs, rowNum) -> makeDirector(rs, jdbcTemplate), filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список режиссёров фильма";

            log.error("findDirectorByFilmId. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Director makeDirector(ResultSet rs, JdbcTemplate jdbcTemplate) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return Director.builder()
                .id(id)
                .name(name)
                .build();
    }
}
