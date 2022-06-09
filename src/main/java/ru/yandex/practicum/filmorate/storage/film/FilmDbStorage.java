package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public FilmDbStorage(DirectorDbStorage directorDbStorage, JdbcTemplate jdbcTemplate) {
        this.directorDbStorage = directorDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film saveFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);
            film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            saveDirectorFilm(film); // положили режиссёра фильма в таблицу film_director

        } catch (Exception e) {
            String message = "Не удалось сохранить фильм";

            log.error("SaveFilm. {}", message);
            throw new RuntimeException(message);
        }
        film.setDirectors();
        return film;
    }

    // сохранет указанного режисёра у фильма в таблицу film_director
    private void saveDirectorFilm(Film film) {
        String sqlAddFilmDirector = "INSERT INTO film_director (film_id, director_id) " +
                "VALUES (?, ?)";
        // проверяем что у фильма указан id существующего режиссёра
        List<Optional<Director>> optDirectors = new ArrayList<>();
        film.getDirectors().forEach(director -> optDirectors.add(directorDbStorage.getDirectorById(director.getId())));
        optDirectors.forEach(opt -> {
            if (opt.isEmpty()) {
                String message = "Неверный id режиссёра";
                log.warn("saveDirectorFilm. {}", message);
                throw new RuntimeException(message);
            }
        });
        optDirectors.forEach(optDirector -> {
            optDirector.ifPresent(director -> jdbcTemplate.update(sqlAddFilmDirector, film.getId(), director.getId()));
        });

    }

    @Override
    public Optional<Film> findById(int id) {
        String sqlQuery = "SELECT id, name, description, release_date, duration, mpa_id FROM film where id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);


            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            String message = "Не удалось получить фильм";

            log.warn("FindFilmById. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT id, name, description, release_date, duration, mpa_id FROM film";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список фильмов";

            log.error("FindAllFilms. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";

        try {
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
        } catch (Exception e) {
            String message = "Не удалось обновить данные фильма";

            log.error("UpdateFilm. {}", message);
            throw new RuntimeException(message);
        }

        return film;
    }

    @Override
    public int saveLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO \"LIKE\" (user_id, film_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (Exception e) {
            String message = "Не удалось добавить лайк фильму";

            log.error("SaveLike. {}", message);
            throw new RuntimeException(message);
        }

        return getCountLikes(filmId);
    }

    @Override
    public int deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM \"LIKE\" WHERE user_id = ? AND film_id = ?";

        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (Exception e) {
            String message = "Не удалось удалить лайк у фильма";

            log.error("DeleteLike. {}", message);
            throw new RuntimeException(message);
        }

        return getCountLikes(filmId);
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "COUNT(l.film_id) AS count_likes " +
                "FROM film f " +
                "LEFT JOIN \"LIKE\" l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY count_likes DESC " +
                "LIMIT ?";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список популярных фильмов";

            log.error("FindPopularFilms. {}", message);
            throw new RuntimeException(message);
        }
    }


    private int getCountLikes(int filmId) {
        String sqlQuery = "SELECT COUNT(film_id) as countLikes FROM \"LIKE\" WHERE film_id = ?";

        try {
            SqlRowSet countRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

            if (countRows.next()) {
                return countRows.getInt("countLikes");
            }
            return 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        } catch (Exception e) {
            String message = "Не удалось посчитать количество лайков фильма";
            log.error("GetCountLikes. {}", message);
            throw new RuntimeException(message);
        }
    }

    // поиск фильма по содержащейся строке в названии фильма
    public List<Film> searchFilmsByTitle(String query, String by) {
        String sqlTitle = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "COUNT(l.film_id) AS count_likes " +
                "FROM film f " +
                "LEFT JOIN \"LIKE\" l ON f.id = l.film_id " +
                "WHERE UPPER (f.name) LIKE '%" + query.toUpperCase() + "%' " +
                "GROUP BY f.id " +
                "ORDER BY count_likes DESC ";
        String sqlDirector = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, d.name," +
                "COUNT(l.film_id) AS count_likes " +
                "FROM film f " +
                "LEFT JOIN \"LIKE\" l ON f.id = l.film_id " +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.film_id " +
                "LEFT JOIN DIRECTOR d ON fd.director_id = d.id " +
                "WHERE UPPER (d.name) LIKE '%" + query.toUpperCase() + "%' " +
                "GROUP BY f.id " +
                "ORDER BY count_likes DESC ";

        try {
            if (by.equals("title")) {
                return jdbcTemplate.query(sqlTitle, this::mapRowToFilm);
            } else if (by.equals("director")) {
                return jdbcTemplate.query(sqlDirector, this::mapRowToFilm);
            }
            // return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilmWithDirector(rs, 1, query));
            return jdbcTemplate.query(sqlTitle, this::mapRowToFilm);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список популярных фильмов";

            log.error("FindPopularFilms. {}", message);
            throw new RuntimeException(message);
        }
    }

    // получение списка фильмов по id режиссёра
    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        String sqlDirectorByLikes = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, d.name, d.id, fd.director_id, " +
                "COUNT(l.film_id) AS count_likes " +
                "FROM film f " +
                "LEFT JOIN \"LIKE\" l ON f.id = l.film_id " +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.film_id " +
                "LEFT JOIN DIRECTOR d ON fd.director_id = d.id " +
                "WHERE d.id = ? " +
                "GROUP BY d.id " +
                "ORDER BY count_likes DESC ";
        String sqlDirectorByYear = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, d.name, d.id, fd.director_id " +
                "FROM FILM AS f " +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.film_id " +
                "LEFT JOIN DIRECTOR d ON fd.director_id = d.id " +
                "WHERE d.id = ? " +
                "GROUP BY d.id " +
                "ORDER BY f.release_date DESC ";
        try {
            if (sortBy.equals("likes")) {
                //   return jdbcTemplate.query(sqlDirectorByLikes, this::mapRowToFilm);
                return jdbcTemplate.query(sqlDirectorByLikes, (rs, rowNum) -> mapRowToFilmWithDirector(rs, (int) directorId, sqlDirectorByLikes), directorId);
            } else {
                return jdbcTemplate.query(sqlDirectorByYear, (rs, rowNum) -> mapRowToFilmWithDirector(rs, (int) directorId, sqlDirectorByYear), directorId);
            }
            // return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilmWithDirector(rs, 1, query));
            //return jdbcTemplate.query(sqlTitle, this::mapRowToFilm);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список популярных фильмов";

            log.error("FindPopularFilms. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        String sqlDirectors = "SELECT * FROM director WHERE ";
        Mpa mpa = Mpa.builder().id(resultSet.getInt("mpa_id")).build();
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .directors(getListOfDirectors(sqlDirectors, (long) i))
                .mpa(mpa)
                .build();
    }

    private Film mapRowToFilmWithDirector(ResultSet resultSet, int i, String sqlDirectors) throws SQLException {
        Film film = mapRowToFilm(resultSet, i);
        List<Director> directors = getListOfDirectors(sqlDirectors, (long) i);
        film.setDirectors(directors);
        return film;
    }

    private List<Director> getListOfDirectors(String sqlDirectors, Long id) {
        return jdbcTemplate.query(sqlDirectors, (result, rowNum) -> filmDirector(result), id);
    }

    public Director filmDirector(ResultSet rs) throws SQLException {
        Long id = rs.getLong("director_id");
        String name = rs.getString("name");
        return Director.builder()
                .id(id)
                .name(name)
                .build();
    }
}
