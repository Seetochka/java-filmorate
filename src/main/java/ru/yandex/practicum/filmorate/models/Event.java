package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private int eventId;
    private long timestamp;
    private int userId;
    private String eventType;   //одно из значениий LIKE, REVIEW или FRIEND
    private String operation;   //одно из значениий REMOVE, ADD, UPDATE
    private int entityId;       //идентификатор сущности, с которой произошло событие
}
