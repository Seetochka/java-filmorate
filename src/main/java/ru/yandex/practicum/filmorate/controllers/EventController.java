package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.services.EventService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * Возвращает ленту событий пользователя.
     */
    @GetMapping("/users/{id}/feed")
    public Collection<Event> getFeed(@PathVariable int id) throws Exception {
        return eventService.getAllByUser(id);
    }
}
