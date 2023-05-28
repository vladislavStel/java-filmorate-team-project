package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
    private final RowMapper<Film> filmMapper;

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

    @Override
    public void deleteFilmById(Long id) {
        if (isNotExistsFilm(id)) {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", id));
        }
        jdbcTemplate.update("DELETE FROM FILM WHERE film_id = ?", id);
        log.info("Фильм удален: id={}", id);
    }

    @Override
    public List<Film> findPopularFilmSortedByGenreAndYear(Long count, int genreId, Integer year) {
        String sqlQuery = "SELECT f.* FROM film f " +
                "LEFT JOIN like_list ll on f.film_id = ll.film_id " +
                "LEFT JOIN genre_list gl ON f.film_id = gl.film_id " +
                "WHERE gl.genre_id = ? AND year(f.releaseDate) = ? " +
                "GROUP BY  f.film_id, gl.genre_id " +
                "ORDER BY COUNT(ll.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, filmMapper, genreId, year, count);
    }

    @Override
    public List<Film> findPopularFilmSortedByGenre(Long count, int genreId) {
        String sqlQuery = "SELECT f.* FROM film f " +
                "LEFT JOIN like_list ll on f.film_id = ll.film_id " +
                "LEFT JOIN genre_list gl ON f.film_id = gl.film_id " +
                "WHERE gl.genre_id = ? " +
                "GROUP BY  f.film_id, gl.genre_id " +
                "ORDER BY COUNT(ll.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery,filmMapper, genreId, count);
    }

    @Override
    public List<Film> findPopularFilmSortedByYear(Long count, Integer year) {
        String sqlQuery = "SELECT f.* FROM film f " +
                "LEFT JOIN like_list ll on f.film_id = ll.film_id " +
                "WHERE year(f.releaseDate) = ? " +
                "GROUP BY  f.film_id " +
                "ORDER BY COUNT(ll.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, filmMapper, year, count);
    }

    @Override
    public List<Film> findPopular(Long count) {
        String sqlQuery = "SELECT f.* FROM film f " +
                "LEFT JOIN like_list ll on f.film_id = ll.film_id " +
                "GROUP BY  f.film_id " +
                "ORDER BY COUNT(ll.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, filmMapper, count);
    }

    @Override
    public List<Long> findFilmsByDirectorSortedByYear(int directorId) {
        String sql =
                "SELECT FILM.film_id " +
                        "FROM FILM " +
                        "LEFT JOIN DIRECTOR_LIST ON FILM.film_id = DIRECTOR_LIST.film_id " +
                        "LEFT JOIN DIRECTOR ON DIRECTOR_LIST.director_id = DIRECTOR.director_id " +
                        "WHERE DIRECTOR.director_id = ? " +
                        "ORDER BY FILM.releaseDate ASC;";

        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("film_id")), directorId);
    }

    @Override
    public List<Long> findFilmsByDirectorSortedByLikes(int directorId) {
        String sql =
                "SELECT FILM.film_id " +
                        "FROM FILM " +
                        "LEFT JOIN LIKE_LIST AS LIKE_LIST ON FILM.film_id = LIKE_LIST.film_id " +
                        "LEFT JOIN DIRECTOR_LIST ON FILM.film_id = DIRECTOR_LIST.film_id " +
                        "LEFT JOIN DIRECTOR ON DIRECTOR_LIST.director_id = DIRECTOR.director_id " +
                        "WHERE DIRECTOR.director_id = ? " +
                        "GROUP BY FILM.film_id " +
                        "ORDER BY COUNT (LIKE_LIST.user_id) DESC;";

        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("film_id")), directorId);
    }

    @Override
    public List<Long> findFilmsByDirectorById(int directorId) {
        String sql =
                "SELECT FILM.film_id " +
                        "FROM FILM " +
                        "LEFT JOIN LIKE_LIST AS likes ON FILM.film_id = LIKE_LIST.film_id " +
                        "LEFT JOIN DIRECTOR_LIST ON FILM.film_id = DIRECTOR_LIST.film_id " +
                        "LEFT JOIN DIRECTOR ON DIRECTOR_LIST.director_id = DIRECTOR.director_id " +
                        "WHERE DIRECTOR.director_id = ? " +
                        "GROUP BY DIRECTOR_LIST.director_id;";

        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("film_id")), directorId);
    }

    @Override
    public List<Film> findFilmsByTitle(String query) {
        String sqlQuery = "SELECT * FROM FILM AS f " +
                "LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN GENRE_LIST AS gl ON f.film_id = gl.film_id " +
                "WHERE LOCATE(UPPER(?), UPPER(f.name)) " +
                "GROUP BY f.film_id, gl.genre_id ";

        return jdbcTemplate.query(sqlQuery, filmMapper, query);
    }

    @Override
    public List<Film> findFilmsByDirector(String query) {
        String sqlQuery = "SELECT * FROM FILM AS f " +
                "LEFT JOIN LIKE_LIST AS ll ON f.film_id = ll.film_id " +
                "LEFT JOIN DIRECTOR_LIST AS dl ON f.film_id = dl.film_id " +
                "LEFT JOIN DIRECTOR AS d ON dl.director_id = d.director_id " +
                "WHERE LOCATE(UPPER(?), UPPER(d.name)) " +
                "GROUP BY f.film_id ";
        return jdbcTemplate.query(sqlQuery, filmMapper, query);
    }

    @Override
    public List<Long> findCommonFilmsWithFriend(Long userId, Long friendId) {
        String sql = "SELECT * " +
                "FROM LIKE_LIST " +
                "JOIN LIKE_LIST LIKES ON LIKES.film_id = LIKE_LIST.film_id " +
                "JOIN FILM on FILM.film_id = LIKES.film_id " +
                "WHERE LIKES.user_id = ? AND LIKE_LIST.user_id = ?";
        log.info("Получен список общих фильмов user: id={} с friend: id={}", userId, friendId);
            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id"), userId, friendId);
    }

}