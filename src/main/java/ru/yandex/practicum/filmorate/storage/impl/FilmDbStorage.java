package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        jdbcTemplate.update("UPDATE FILM SET mpa_id = ? WHERE film_id = ?",
                film.getMpa().getId(),
                film.getId());
        log.info("Добавлен новый фильм: id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (isNotExistsFilm(film.getId())) {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", film.getId()));
        }
        String sqlQuery = "UPDATE FILM SET name = ?," +
                " description = ?," +
                " releaseDate = ?," +
                " duration = ?," +
                " mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        log.info("Данные фильма обновлены: id={}", film.getId());
        return film;
    }

    @Override
    public List<Long> findAllFilms() {
        return jdbcTemplate.query("SELECT film_id FROM FILM ORDER BY film_id;",
                ((rs, rowNum) -> rs.getLong("film_id")));
    }

    @Override
    public Film findFilmById(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE film_id = ?", id);
        if (filmRows.next()) {
            Film film = Film.builder()
                .id(filmRows.getLong("film_id"))
                .name(filmRows.getString("name"))
                .description(filmRows.getString("description"))
                .releaseDate(filmRows.getDate("releaseDate").toLocalDate())
                .duration(filmRows.getInt("duration"))
                .mpa(new Mpa(filmRows.getInt("mpa_id")))
                .build();
            log.info("Найден фильм: id={}", id);
            return film;
        } else {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", id));
        }
    }

    @Override
    public boolean isNotExistsFilm(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE film_id = ?", id);
        return !filmRows.next();
    }


    @Override
    public void delete(Film film) {
        if (isNotExistsFilm(film.getId())) {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", film.getId()));
        }
        jdbcTemplate.update("DELETE FROM FILM WHERE film_id = ?", film.getId());
        log.info("Фильм удален: id={}", film.getId());
    }

}