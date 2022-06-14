package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class LikeReview {
    @NotNull
    int userId;
    @NotNull
    int reviewId;
    boolean isLike;
}
