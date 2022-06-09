package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


// хранит онформацию о режиссёре
@Builder
@Data
public class Director {

    Long id;
    @NotNull
    String name;

}
