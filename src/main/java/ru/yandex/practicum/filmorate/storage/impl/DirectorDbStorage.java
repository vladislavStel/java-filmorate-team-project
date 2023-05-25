package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM DIRECTOR", ((rs, rowNum) -> new Director(
                    rs.getInt("director_id"),
                    rs.getString("name"))
        ));
    }

    @Override
    public Director findDirectorById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT name FROM DIRECTOR WHERE DIRECTOR_ID = ?", id);
        if (userRows.next()) {
            Director director = new Director(id, userRows.getString("name"));
            log.info("Найден жанр по Id = {} ", director);
            return director;
        } else {
            throw new ObjectNotFoundException(String.format("Не найден режиссер: id=%d", id));
        }
    }

    @Override
    public void saveDirectorByFilm(Film film) {
        if (film.getDirectors() != null) {
            List<Director> directorList = new ArrayList<>(film.getDirectors());
            jdbcTemplate.batchUpdate("INSERT INTO DIRECTOR_LIST (film_id, director_id) VALUES (?, ?) ",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, film.getId());
                            ps.setLong(2, directorList.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return film.getDirectors().size();
                        }
                    });
        }
    }

    @Override
    public void deleteDirectorByFilm(Film film) {
        jdbcTemplate.update("DELETE FROM DIRECTOR_LIST WHERE film_id = ?", film.getId());
    }

    public Set<Director> findFilmDirectors(Long filmId) {
        return new HashSet<>(jdbcTemplate.query("SELECT DIRECTOR.director_id, name FROM DIRECTOR_LIST" +
                        " JOIN DIRECTOR ON DIRECTOR_LIST.director_id = DIRECTOR.director_id" +
                        " WHERE film_id = ? ORDER BY DIRECTOR.director_id", (rs, rowNum) -> new Director(
                        rs.getInt("director_id"),
                        rs.getString("name")),
                filmId
        ));
    }

    @Override
    public Director save(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTOR")
                .usingGeneratedKeyColumns("director_id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue());
        log.info("Добавлен новый режиссер: id={}", director.getId());
        return director;
    }

    @Override
    public Director update(Director director) {
        if (isNotExistsDirector(director.getId())) {
            throw new ObjectNotFoundException(String.format("Режиссер не найден: id=%d", director.getId()));
        }
        String sqlQuery = "UPDATE DIRECTOR SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        log.info("Данные режиссера обновлены: id={}", director.getId());
        return director;
    }

    @Override
    public void delete(int id) {
        if (isNotExistsDirector(id)) {
            throw new ObjectNotFoundException(String.format("Режиссер не найден: id=%d", id));
        }
        jdbcTemplate.update("DELETE FROM DIRECTOR WHERE director_id = ?", id);
        log.info("Режиссер удален: id={}", id);
    }

    @Override
    public boolean isNotExistsDirector(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM DIRECTOR WHERE director_id = ?", id);
        return !filmRows.next();
    }

}