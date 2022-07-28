package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.event.EventDBStorage;
import ru.yandex.practicum.filmorate.models.Event;

import java.util.Collection;

/**
 * Класс для операций с событиями
 */
@Service
public class EventService {
    private final EventDBStorage eventStorage;

    /**
     * Конструктор сервиса
     */
    @Autowired
    public EventService(EventDBStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    /**
     * Получение списка всех событий пользователя
     */
    public Collection<Event> getAllByUser(Integer userId) throws Exception {
        return eventStorage.getAllByUser(userId);
    }

    /**
     * Запись события
     */
    public void saveEvent(Event event) {
        eventStorage.saveEvent(event);
    }
}
