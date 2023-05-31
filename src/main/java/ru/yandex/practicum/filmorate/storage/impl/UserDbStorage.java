package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper;

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
    public Set<Long> findLikeListByUserId(Long id) {
        String sql = "SELECT film_id FROM LIKE_LIST WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id"), id));
    }
    @Override
    public Set<Long> findLikeList() {
        String sql = "SELECT * FROM LIKE_LIST ";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id")));
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