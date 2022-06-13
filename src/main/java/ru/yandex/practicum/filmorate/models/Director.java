package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Хранит информацию о режиссёре
 */
@Builder
@Data
public class Director {

    private int id;
    @NotNull
    private String name;

}
