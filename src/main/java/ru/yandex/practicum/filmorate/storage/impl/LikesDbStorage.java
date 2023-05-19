package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    public void saveLike(Long filmId, Long userId) {
        jdbcTemplate.update("INSERT INTO LIKE_LIST (film_id, user_id) VALUES (? , ?)", filmId, userId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        jdbcTemplate.update("DELETE FROM LIKE_LIST WHERE user_id = ? AND film_id = ?", userId, filmId);
        log.info("Пользователь id={} удалил лайк у фильма id={}", userId, filmId);
    }

    @Override
    public List<Long> findPopular(Long count) {
        String sql = "SELECT film_id FROM LIKE_LIST GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Long.class, count);
    }

}