package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    private int reviewId;
    @NotNull
    @NotBlank
    private String content;
    @NotNull
    @JsonProperty("isPositive")
    private boolean isPositive;
    @NotNull
    private int userId;
    @NotNull
    private int filmId;
    private int useful;
}
