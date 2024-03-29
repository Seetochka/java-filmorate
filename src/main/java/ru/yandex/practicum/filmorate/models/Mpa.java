package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Модель рейтинга
 */
@Data
@Builder
public class Mpa {
    private int id;
    private String name;

    @JsonCreator
    public Mpa(@JsonProperty("id") int id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }
}
