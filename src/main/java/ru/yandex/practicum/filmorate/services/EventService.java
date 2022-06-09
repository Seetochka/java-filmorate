package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.event.EventDBStorage;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

//Класс для операций с событиями
@Service
public class EventService {
    private EventDBStorage eventStorage;
    private UserStorage userStorage;

    //Конструктор сервиса
    @Autowired
    public EventService(@Qualifier("UserDbStorage") UserStorage userStorage,
                        @Qualifier("EventDBStorage") EventDBStorage eventStorage){
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    //Получение списка всех событий пользователя
    public List<Event> getAllByUser(Integer userId){
        userStorage.findById(userId);
        return eventStorage.getAllByUser(userId);
    };
}
