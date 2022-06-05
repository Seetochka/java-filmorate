package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mpa {
    private int id;

    @JsonCreator
    public Mpa(@JsonProperty("id") int id) {
        this.id = id;
    }
}
