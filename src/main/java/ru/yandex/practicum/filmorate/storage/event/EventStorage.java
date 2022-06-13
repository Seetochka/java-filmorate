package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Event;

import java.util.Collection;

public interface EventStorage {
    Collection<Event> getAllByUser(Integer userId) throws ModelNotFoundException;

    void saveEvent(Event event);
}
