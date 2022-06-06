package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friend {
    private int userId;
    private int friendId;
    private boolean status;
}
