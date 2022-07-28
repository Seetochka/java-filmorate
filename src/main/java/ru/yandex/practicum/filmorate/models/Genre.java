package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Модель жанра
 */
@Data
@Builder
public class Genre {
    private int id;
    private String name;

    @JsonCreator
    public Genre(@JsonProperty("id") int id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }
}
