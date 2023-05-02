package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;

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
        } else throw new ObjectNotFoundException(String.format("Не найден жанр: id=%d", id));
    }

    @Override
    public void saveGenresByFilm(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO GENRE_LIST (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId()
                );
            }
        }
    }

    @Override
    public void deleteGenresByFilm(Film film) {
        jdbcTemplate.update("DELETE FROM GENRE_LIST WHERE film_id = ?", film.getId());
    }

    @Override
    public HashSet<Genre> findFilmGenres(Long filmId) {
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