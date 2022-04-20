package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private static int lastId = 1;

    private int id;
    @NotNull
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    @NotNull
    @Past(groups = LocalDate.class)
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        id = getLastId();
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public static int getLastId() {
        return lastId++;
    }
}
