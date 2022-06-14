package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Event;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Event> getAllByUser(Integer userId) throws ModelNotFoundException {
        Collection<Event> ret = new ArrayList<>();

        try {
            SqlRowSet eventRows = jdbcTemplate.queryForRowSet("select * from event where user_id = ?", userId);

            while (eventRows.next()) {
                ret.add(Event.builder()
                        .eventId(eventRows.getInt("event_id"))
                        .timestamp(eventRows.getTimestamp("time_stamp").toInstant().getEpochSecond())
                        .userId(eventRows.getInt("user_id"))
                        .eventType(eventRows.getString("event_type"))
                        .operation(eventRows.getString("operation"))
                        .entityId(eventRows.getInt("entity_id"))
                        .build());
            }

            return ret;
        } catch (EmptyResultDataAccessException e) {
            log.error("Не найдено событий для пользователя " + userId);
            throw new ModelNotFoundException("Не найдено событий для пользователя " + userId);
        }
    }

    @Override
    public void saveEvent(Event event) {
        try {
            jdbcTemplate.update("insert into event (user_id, event_type, operation, entity_id) values (?,?,?,?)",
                    event.getUserId(), event.getEventType(), event.getOperation(), event.getEntityId());
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка логирования события userId = " + event.getUserId() +
                    ", eventType = " + event.getEventType() +
                    ", operation = " + event.getOperation() +
                    ", entityId = " + event.getEntityId());
        }
    }
}
