package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Slf4j
@Repository
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper;
    private final RowMapper<Film> filmMapper;

    @Override
    public List<User> findAllUsers() {
        var sqlQuery = "SELECT user_id, login, name, birthday, email FROM USERS";
        return jdbcTemplate.query(sqlQuery, userMapper);
    }

    @Override
    public User findUserById(Long id) {
        var sqlQuery = "SELECT user_id, login, name, birthday, email FROM USERS WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, userMapper, id);
    }

    @Override
    public Map<Long, List<Film>> findIdToFilms() {
        String sql = "SELECT * FROM FILM " +
                "JOIN LIKE_LIST on FILM.film_id = LIKE_LIST.film_id ";
        List<Pair<Long, Film>> pairs = jdbcTemplate.query(sql,
                (rs, rowNum) -> Pair.of(rs.getLong("user_id"), filmMapper.mapRow(rs, rowNum)));

        return pairs.stream()
                .collect(Collectors.groupingBy(Pair::getFirst, mapping(Pair::getSecond, toList())));
    }

    @Override
    public User save(User user) {
        var simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        log.info("Добавлен новый пользователь: id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        var sqlQuery = "UPDATE USERS SET login = ?, name = ?, birthday = ?, email = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getEmail(),
                user.getId());
        log.info("Обновлены данные пользователя: id={}", user.getId());
        return user;
    }

    @Override
    public void delete(User user) {
        if (isNotExistsUser(user.getId())) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", user.getId()));
        }
        jdbcTemplate.update("DELETE FROM USERS WHERE user_id = ?", user.getId());
        log.info("Пользователь удален: id={}", user.getId());
    }

    @Override
    public void deleteUserById(Long id) {
        if (isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        jdbcTemplate.update("DELETE FROM USERS WHERE user_id = ?", id);
        log.info("Пользователь удален: id={}", id);
    }

    @Override
    public boolean isNotExistsUser(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE user_id = ?", id);
        return !userRows.next();
    }

}