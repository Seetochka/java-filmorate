package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * Модель пользователя
 */
@Data
public class User {
    private int id;
    @NotNull
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    @NotNull
    @Past
    private LocalDate birthday;
    private Set<Integer> friends = new TreeSet<>();

    /**
     * Сохранение в друзья
     */
    public void saveFriend(int id) {
        friends.add(id);
    }

    /**
     * Удаление из друзей
     */
    public void deleteFriend(int id) {
        friends.remove(id);
    }

    /**
     * Получение количества друзей
     */
    public int getCountFriends() {
        return friends.size();
    }
}
