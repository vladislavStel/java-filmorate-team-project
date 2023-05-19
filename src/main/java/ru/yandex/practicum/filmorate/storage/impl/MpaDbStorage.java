package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAllMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA", (rs, rowNum) -> new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("name"))
        );
    }

    @Override
    public Mpa findMpaById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT name FROM MPA WHERE mpa_id = ?", id);
        if (userRows.next()) {
            log.info("Mpa найден: {}", id);
            return new Mpa(id, userRows.getString("name"));
        } else {
            throw new ObjectNotFoundException(String.format("Mpa не найден: id=%d", id));
        }
    }

}