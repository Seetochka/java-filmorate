package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

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

            film.setId(
                    Objects.requireNonNull(keyHolder.getKey()).intValue()
            );

            if (film.getGenres() != null) {
                saveFilmGenre(film.getId(), film.getGenres());

                film.setGenres(findGenresByFilmId(film.getId()));
            }
        } catch (Exception e) {
            String message = "Не удалось сохранить фильм";

            log.error("SaveFilm. {}", message);
            throw new RuntimeException(message);
        }

        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        String sqlQuery = "SELECT f.id, f.name as film_name, f.description, f.release_date, " +
                "f.duration, f.mpa_id, m.name as mpa_name " +
                "FROM film f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";

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
        String sqlQuery = "SELECT f.id, f.name as film_name, f.description, f.release_date, " +
                "f.duration, f.mpa_id, m.name as mpa_name " +
                "FROM film f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id";

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

            if (film.getGenres() != null) {
                if (findCountGenreForFilm(film.getId()) > 0) {
                    deleteFilmGenres(film.getId());
                }

                saveFilmGenre(film.getId(), film.getGenres());

                film.setGenres(findGenresByFilmId(film.getId()));
            }
        } catch (Exception e) {
            String message = "Не удалось обновить данные фильма";

            log.error("UpdateFilm. {}", message);
            throw new RuntimeException(message);
        }

        return film;
    }

    @Override
    public int saveLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO `like` (user_id, film_id) VALUES (?, ?)";

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
        String sqlQuery = "DELETE FROM `like` WHERE user_id = ? AND film_id = ?";

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
        String sqlQuery = "SELECT f.id, f.name as film_name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.name as mpa_name, COUNT(l.film_id) AS count_likes " +
                "FROM film f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN `like` l ON f.id = l.film_id " +
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
        String sqlQuery = "SELECT COUNT(film_id) as countLikes FROM `like` WHERE film_id = ?";

        try {
            SqlRowSet countRows =  jdbcTemplate.queryForRowSet(sqlQuery, filmId);

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

    private void saveFilmGenre(int filmId, Collection<Genre> genres) {
        String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : genres) {
            try {
                jdbcTemplate.update(sqlQuery, filmId, genre.getId());
            } catch (DuplicateKeyException e) {
                continue;
            } catch (Exception e) {
                String message = "Не удалось сохранить привязку жанра к фильму";

                log.error("SaveFilmGenre. {}", message);
                throw new RuntimeException(message);
            }
        }
    }

    private void deleteFilmGenres(int filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";

        try {
            jdbcTemplate.update(sqlQuery, filmId);

        } catch (Exception e) {
            String message = "Не удалось удалить привязки жанров к фильму";

            log.error("DeleteFilmGenres. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Collection<Genre> findGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT g.id, g.name " +
                "FROM genre g " +
                "INNER JOIN film_genre fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список жанров фильма";

            log.error("FindGenresByFilmId. {}", message);
            throw new RuntimeException(message);
        }
    }

    private int findCountGenreForFilm(int filmId) {
        String sqlQuery = "SELECT COUNT(genre_id) as countGenres FROM film_genre WHERE film_id = ?";

        try {
            SqlRowSet countRows =  jdbcTemplate.queryForRowSet(sqlQuery, filmId);

            if (countRows.next()) {
                return countRows.getInt("countGenres");
            }

            return 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        } catch (Exception e) {
            String message = "Не удалось посчитать количество жанров фильма";

            log.error("FindCountGenreForFilm. {}", message);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT f.id, f.name as film_name, f.description," +
                "f.release_date, f.duration, f.mpa_id, m.name as mpa_name" +
                " FROM film f " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                "WHERE f.id IN (SELECT film_id " +
                "              FROM `like` l " +
                "              WHERE l.user_id = ?) " +
                "AND f.id IN (SELECT film_id " +
                "             FROM `like` l " +
                "             WHERE l.user_id = ?)";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
        } catch (Exception e) {
            String message = "Не удалось найти общие фильмы";

            log.error("GetCommonFilms. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();


        Collection<Genre> genres = findGenresByFilmId(resultSet.getInt("id"));
        if (genres != null && genres.isEmpty()) genres = null;
        
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
