package ru.yandex.practicum.filmorate.storage.rewie;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.models.Review;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewDbStorageTest {

    private final ReviewDbStorage storage;

    @Test
    public void testGetReviewById() {
        Optional<Review> reviewOptional = storage.findById(1);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("content", "So good")
                                .hasFieldOrPropertyWithValue("useful", 0)
                );
    }

    @Test
    public void testGetAll() {
       Collection<Review> reviews = storage.findAll(10);
        assertThat(reviews).hasSize(3);
        reviews = storage.findAll(2);
        assertThat(reviews).hasSize(2);
    }

    @Test
    public void testCreateReview() {
        Collection<Review> reviews = storage.findAll(10);
        assertThat(reviews).hasSize(3);
        Review newReview = Review.builder().isPositive(false).userId(1).filmId(2).content("Not Bad").build();
        storage.saveReview(newReview);
        reviews = storage.findAll(10);
        assertThat(reviews).hasSize(4);
        Optional<Review> reviewOptional = storage.findById(4);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("content", "Not Bad")
                                .hasFieldOrPropertyWithValue("useful", 0)
                                .hasFieldOrPropertyWithValue("isPositive", false)
                );
    }

    @Test
    public void testUpdateReview() {
        Optional<Review> reviewOptional = storage.findById(1);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("content", "So good")
                                .hasFieldOrPropertyWithValue("useful", 0)
                );
        Review review = Review.builder().reviewId(1).isPositive(false).userId(1).filmId(1).content("So So good").build();
        storage.updateReview(review);
        reviewOptional = storage.findById(1);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("content", "So So good")
                                .hasFieldOrPropertyWithValue("useful", 0)
                );
    }

    @Test
    public void testDeleteReview() {
        Collection<Review> reviews = storage.findAll(10);
        assertThat(reviews).hasSize(3);
        Review newReview = Review.builder().isPositive(false).userId(1).filmId(2).content("Not Bad").build();
        storage.deleteReview(1);
        reviews = storage.findAll(10);
        assertThat(reviews).hasSize(2);
    }

    @Test
    public void testFindByFilmId() {
        Collection<Review> reviews = storage.findByFilmId(1, 10);
        assertThat(reviews).hasSize(2);
        reviews = storage.findByFilmId(1, 1);
        assertThat(reviews).hasSize(1);
    }

}