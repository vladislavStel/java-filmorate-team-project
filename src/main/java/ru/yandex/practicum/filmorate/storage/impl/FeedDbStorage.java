package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveEvent(Long userId, String eventType, String operation, Long entityId) {
        String sqlQuery = "INSERT INTO EVENT (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, eventType, operation, entityId);
        log.info("Событие для пользователя id = {} добавлено в базу данных", userId);
    }

    @Override
    public List<Event> findFeed(Long id) {
        String sqlQuery = "SELECT * FROM EVENT WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Event(
                        rs.getTimestamp("event_time").getTime(),
                        rs.getLong("user_id"),
                        rs.getString("event_type"),
                        rs.getString("operation"),
                        rs.getLong("event_id"),
                        rs.getLong("entity_id")),
                id
        );
    }
}
