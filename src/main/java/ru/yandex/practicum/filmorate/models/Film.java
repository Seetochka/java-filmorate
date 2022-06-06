package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validators.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * Модель фильма
 */
@Data
@Builder
public class Film {
    private int id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    @Length(max = 200)
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @NotNull
    private Mpa mpa;
    @JsonIgnore
    private Set<Integer> likes = new TreeSet<>();

    /**
     * Сохранение лайка пользователя
     */
    public void saveLike(int id) {
        likes.add(id);
    }

    /**
     * Удаление лайка пользователя
     */
    public void deleteLike(int id) {
        likes.remove(id);
    }

    /**
     * Получение количества лайков
     */
    @JsonIgnore
    public int getCountLikes() {
        return likes.size();
    }
}
