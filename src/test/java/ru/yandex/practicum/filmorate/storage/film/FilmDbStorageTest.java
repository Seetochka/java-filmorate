package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;

    @Test
    void testSaveFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(Mpa.builder().id(2).build())
                .build();

        film = filmStorage.saveFilm(film);
        Optional<Mpa> mpaOptional = mpaStorage.findById(2);
        assertThat(mpaOptional).isPresent();
        film.setMpa(mpaOptional.get());

        Optional<Film> filmOptional = filmStorage.findById(film.getId());

        assertThat(filmOptional).isPresent();
        assertThat(film).isEqualTo(filmOptional.get());
    }

    @Test
    void testFindById() {
        Optional<Film> filmOptional = filmStorage.findById(3);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 3)
                )
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "name")
                )
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "description")
                );
    }

    @Test
    void testFindAll() {
        Collection<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(3);
    }

    @Test
    void testUpdateFilm() {
        Optional<Film> filmOptional = filmStorage.findById(2);
        assertThat(filmOptional).isPresent();

        Film film = filmOptional.get();
        film.setName("Update Name");

        filmStorage.updateFilm(film);

        Optional<Film> updatedFilmOptional = filmStorage.findById(2);
        assertThat(updatedFilmOptional).isPresent();

        assertThat(film).isEqualTo(updatedFilmOptional.get());
    }

    @Test
    void testSaveLike() {
        Optional<Film> filmOptional = filmStorage.findById(1);
        assertThat(filmOptional).isPresent();

        filmStorage.saveLike(1, 1);
        filmStorage.saveLike(1, 2);

        Collection<Film> popularFilms = filmStorage.findPopularFilms(1);

        assertThat(popularFilms).hasSize(1);
        assertThat(popularFilms).contains(filmOptional.get());
    }

    @Test
    void testDeleteLike() {
        Optional<Film> filmOptional = filmStorage.findById(1);
        assertThat(filmOptional).isPresent();

        filmStorage.deleteLike(1, 1);
        filmStorage.deleteLike(1, 2);

        Collection<Film> popularFilms = filmStorage.findPopularFilms(1);

        assertThat(popularFilms).hasSize(1);
        assertThat(popularFilms).doesNotContain(filmOptional.get());
    }

    @Test
    void testFindPopularFilms() {
        Optional<Film> filmOptional1 = filmStorage.findById(3);
        Optional<Film> filmOptional2 = filmStorage.findById(1);
        assertThat(filmOptional1).isPresent();
        assertThat(filmOptional2).isPresent();

        filmStorage.saveLike(3, 1);
        filmStorage.saveLike(3, 2);

        Collection<Film> popularFilms = filmStorage.findPopularFilms(2);

        assertThat(popularFilms).hasSize(2);
        assertThat(popularFilms).isEqualTo(List.of(filmOptional1.get(), filmOptional2.get()));
    }

    @Test
    void testFindCommonFilms() {
        //filmStorage.getCommonFilms(2, 1);

        Collection<Film> commonFilms = filmStorage.getCommonFilms(1,2);
        //Collection<User> commonFilms = filmStorage.findCommonFriends(1, 2);

        assertThat(commonFilms).hasSize(1);
        assertThat(filmStorage.findById(2)).isEqualTo(2);
    }
}
