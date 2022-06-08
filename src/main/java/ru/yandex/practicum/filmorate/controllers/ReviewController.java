package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;


/**
 * Контроллер для работы с отзывами
 */

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public Review saveReview(@Valid @RequestBody Review review, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("SaveReview. {}", message);
            throw new ValidationException(message);
        }

        Review createdReview = service.saveReview(review);

        log.info("SaveReview. Фильм с id {} успешно добавлен", review.getReviewId());
        return createdReview;
    }

    @GetMapping()
    public Collection<Review> findAllByFilmId(@RequestParam(defaultValue = "10", required = false) Integer count,
                                              @RequestParam(required = false) Integer filmId)
            throws ModelNotFoundException, IncorrectParameterException {

        return service.findByFilmId(Optional.ofNullable(filmId), count);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable("id") int reviewId) throws ModelNotFoundException {
        return service.findById(reviewId);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review, BindingResult bindingResult)
            throws ValidationException, ModelNotFoundException {
        if (bindingResult.hasErrors()) {
            String message = getStringErrors(bindingResult);

            log.warn("UpdateReview. " + message);
            throw new ValidationException(message);
        }

        Review updatedReview = service.updateReview(review);

        log.info("UpdateReview. Отзыв с id {} успешно обновлен", review.getReviewId());
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") int reviewID) throws ModelNotFoundException {
        service.deleteFilm(reviewID);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLikeReview(@PathVariable("id") int reviewID, @PathVariable("userId") int userId)
            throws ModelNotFoundException {
        service.putLikeReview(reviewID, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislikeReview(@PathVariable("id") int reviewID, @PathVariable("userId") int userId)
            throws ModelNotFoundException {
        service.putDislikeReview(reviewID, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable("id") int reviewID, @PathVariable("userId") int userId)
            throws ModelNotFoundException {
        service.deleteLikeReview(reviewID, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable("id") int reviewID, @PathVariable("userId") int userId)
            throws ModelNotFoundException {
        service.deleteDislikeReview(reviewID, userId);
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(element -> String.format("%s: %s; ", element.getField(), element.getDefaultMessage()))
                .reduce("", (partialString, element) -> partialString + element);
    }
}
