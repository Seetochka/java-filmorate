package ru.yandex.practicum.filmorate.storage.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.models.Event;

import java.util.ArrayList;
import java.util.List;

//Класс для работы с событиями
@Component("EventDBStorage")
@Repository
public class EventDBStorage implements EventStorage {
    protected static final Logger log = LoggerFactory.getLogger(EventDBStorage.class);
    private final JdbcTemplate jdbcTemplate;

    //Конструктор класса
    public EventDBStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    //Получение списка всех событий пользователя
    @Override
    public List<Event> getAllByUser(Integer userId) {
        List<Event> ret = new ArrayList<>();
        try{
            SqlRowSet eventRows = jdbcTemplate.queryForRowSet("select * from event where user_id = ?", userId);

            while (eventRows.next()) {
                ret.add(new Event(eventRows.getInt("event_id"),
                                  eventRows.getTimestamp("time_stamp").toInstant().getEpochSecond(),
                                  eventRows.getInt("user_id"),
                                  eventRows.getString("event_type"),
                                  eventRows.getString("operation"),
                                  eventRows.getInt("entity_id")));
            }

            return ret;

        } catch (EmptyResultDataAccessException e) {
            log.error("Не найдено событий для пользователя " + userId);
            throw new EntityNotFoundException("Не найдено событий для пользователя " + userId);
        }
    }

    //Внесение записи о событии
    @Override
    public void log(Integer userId, String eventType, String operation, Integer entityId) {
        try {
            jdbcTemplate.update("insert into event (user_id, event_type, operation, entity_id) values (?,?,?,?)",
                    userId, eventType, operation, entityId);

        }catch (DataIntegrityViolationException e){
            log.error("Ошибка логирования события userId = " + userId +
                    ", eventType = " + eventType +
                    ", operation = " + operation +
                    ", entityId = " + entityId);
        }
    }
}
