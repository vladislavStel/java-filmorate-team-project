package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedDbStorage feedDbStorage;

    @Override
    public List<User> findFriends(Long id) {
        return jdbcTemplate.query("SELECT friend_id, login, name, birthday, email " +
                        "FROM FRIEND_LIST AS F " +
                        "JOIN USERS AS U ON F.friend_id = U.user_id " +
                        "WHERE F.user_id = ?", (rs, rowNum) -> new User(
                        rs.getLong("friend_id"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate(),
                        rs.getString("email")),
                id
        );
    }

    @Override
    public void save(Long userId, Long friendId) {
        jdbcTemplate.update("INSERT INTO FRIEND_LIST (user_id, friend_id) VALUES (?, ?)", userId, friendId);
        log.info("Пользователь id={} добавил пользователя id={} в друзья", userId, friendId);
        feedDbStorage.saveEvent(userId, "FRIEND", "ADD", friendId);
    }

    @Override
    public void delete(Long userId, Long friendId) {
        jdbcTemplate.update("DELETE FROM FRIEND_LIST WHERE user_id = ? AND friend_id = ?", userId, friendId);
        log.info("Пользователь id={} удалил пользователя id={} из друзей", userId, friendId);
        feedDbStorage.saveEvent(userId, "FRIEND", "REMOVE", friendId);
    }

}