package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validators.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private static int lastId = 1;

    private int id;
    @NotNull
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive
    private int duration;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        id = getLastId();
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public static int getLastId() {
        return lastId++;
    }
}
