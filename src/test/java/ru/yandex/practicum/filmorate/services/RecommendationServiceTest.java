package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RecommendationServiceTest {
    private final RecommendationService recommendationservice;
    private final FilmDbStorage filmStorage;

    @Test
    void findRecommendations() {
        Film film = Film.builder()
                .name("recommend")
                .description("recommend description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(Mpa.builder().id(4).name("R").build())
                .build();

        film = filmStorage.saveFilm(film);

        filmStorage.saveLike(2, 1);
        filmStorage.saveLike(2, 2);
        filmStorage.saveLike(film.getId(), 2);

        Optional<Film> recommendationOptional = recommendationservice.findRecommendations(1).stream().findFirst();

        assertThat(recommendationOptional).isPresent();
        assertThat(recommendationOptional.get()).isEqualTo(film);
    }
}
