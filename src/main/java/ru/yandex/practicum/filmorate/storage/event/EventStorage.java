package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.models.Event;

import java.util.List;

public interface EventStorage {
    //Получение списка всех событий пользователя
    List<Event> getAllByUser(Integer userId);

    //Внесение записи о событии
    void log(Integer userId, String eventType, String operation, Integer entityId);
}
