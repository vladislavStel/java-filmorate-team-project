package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAllGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE", ((rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"))
        ));
    }

    public Genre findGenreById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT name FROM GENRE WHERE GENRE_ID = ?", id);
        if (userRows.next()) {
            Genre genre = new Genre(id, userRows.getString("name"));
            log.info("Найден жанр по Id = {} ", genre);
            return genre;
        } else {
            throw new ObjectNotFoundException(String.format("Не найден жанр: id=%d", id));
        }
    }

    @Override
    public void saveGenresByFilm(Film film) {
        if (film.getGenres() != null) {
            List<Genre> genreList = new ArrayList<>(film.getGenres());
            jdbcTemplate.batchUpdate("INSERT INTO GENRE_LIST (film_id, genre_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genreList.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return film.getGenres().size();
                }
            });
        }
    }

    @Override
    public void deleteGenresByFilm(Film film) {
        jdbcTemplate.update("DELETE FROM GENRE_LIST WHERE film_id = ?", film.getId());
    }

    @Override
    public Set<Genre> findFilmGenres(Long filmId) {
        return new HashSet<>(jdbcTemplate.query("SELECT GENRE.genre_id, name FROM GENRE_LIST" +
                        " JOIN GENRE ON GENRE_LIST.genre_id = GENRE.genre_id" +
                        " WHERE film_id = ?" +
                        " ORDER BY GENRE.genre_id", (rs, rowNum) -> new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("name")),
                filmId
        ));
    }

}