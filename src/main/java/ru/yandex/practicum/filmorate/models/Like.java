package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

/**
 * Модель лайка
 */
@Data
@Builder
public class Like {
    private int userId;
    private int filmId;
}
