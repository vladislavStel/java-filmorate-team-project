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
@Repository
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmMapper;

    @Override
    public List<Long> findAllFilms() {
        return jdbcTemplate.query("SELECT film_id FROM FILM ORDER BY film_id;",
                ((rs, rowNum) -> rs.getLong("film_id")));
    }

    @Override
    public Film findFilmById(Long id) {
        var filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE film_id = ?", id);
        if (filmRows.next()) {
            var film = Film.builder()
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
    public List<Long> findPopularFilmsSortedByGenre(int genreId) {
        var withoutLikes = "SELECT fg.film_id FROM (SELECT FILM.film_id FROM FILM " +
                "INNER JOIN GENRE_LIST as g ON FILM.film_id = g.film_id WHERE genre_id = ?) AS fg";
        var withLikes = withoutLikes + " " +
                "INNER JOIN LIKE_LIST l on fg.film_id = l.film_id GROUP BY l.film_id ORDER BY COUNT(user_id) DESC";

        return filterPopularFilms(withLikes, withoutLikes, genreId);
    }

    @Override
    public List<Long> findPopularFilmsSortedByYear(int count, int year) {
        var withoutLikes = "SELECT fy.film_id FROM (SELECT film_id FROM FILM WHERE year(releaseDate) = ?) AS fy ";
        var withLikes = withoutLikes + " " +
                "INNER JOIN LIKE_LIST l on fy.film_id = l.film_id GROUP BY l.film_id " +
                "ORDER BY COUNT(user_id) DESC limit " + count;
        return filterPopularFilms(withLikes, withoutLikes, year);
    }

    @Override
    public List<Long> findPopularFilms(int count) {
        String sql = "SELECT film_id FROM LIKE_LIST GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Long.class, count);
    }

    @Override
    public List<Long> findFilmsByDirectorSortedByYear(int directorId) {
        var sql = "SELECT FILM.film_id FROM FILM " +
                "LEFT JOIN DIRECTOR_LIST ON FILM.film_id = DIRECTOR_LIST.film_id " +
                "LEFT JOIN DIRECTOR ON DIRECTOR_LIST.director_id = DIRECTOR.director_id " +
                "WHERE DIRECTOR.director_id = ? " +
                "ORDER BY FILM.releaseDate ASC;";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("film_id")), directorId);
    }

    @Override
    public List<Long> findFilmsByDirectorSortedByLikes(int directorId) {
        var sql = "SELECT FILM.film_id FROM FILM " +
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
        var sql = "SELECT FILM.film_id FROM FILM " +
                        "LEFT JOIN LIKE_LIST AS likes ON FILM.film_id = LIKE_LIST.film_id " +
                        "LEFT JOIN DIRECTOR_LIST ON FILM.film_id = DIRECTOR_LIST.film_id " +
                        "LEFT JOIN DIRECTOR ON DIRECTOR_LIST.director_id = DIRECTOR.director_id " +
                        "WHERE DIRECTOR.director_id = ? " +
                        "GROUP BY DIRECTOR_LIST.director_id;";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("film_id")), directorId);
    }

    @Override
    public List<Film> findFilmsByTitle(String query) {
        var sqlQuery = "SELECT * FROM FILM AS f " +
                "LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN GENRE_LIST AS gl ON f.film_id = gl.film_id " +
                "WHERE LOCATE(UPPER(?), UPPER(f.name)) " +
                "GROUP BY f.film_id, gl.genre_id ";
        return jdbcTemplate.query(sqlQuery, filmMapper, query);
    }

    @Override
    public List<Film> findFilmsByDirector(String query) {
        var sqlQuery = "SELECT * FROM FILM AS f " +
                "LEFT JOIN DIRECTOR_LIST AS dl ON f.film_id = dl.film_id " +
                "LEFT JOIN DIRECTOR AS d ON dl.director_id = d.director_id " +
                "WHERE LOCATE(UPPER(?), UPPER(d.name)) ";
        return jdbcTemplate.query(sqlQuery, filmMapper, query);
    }

    @Override
    public List<Long> findCommonFilmsWithFriend(Long userId, Long friendId) {
        var sql = "SELECT * FROM LIKE_LIST " +
                "JOIN LIKE_LIST LIKES ON LIKES.film_id = LIKE_LIST.film_id " +
                "JOIN FILM on FILM.film_id = LIKES.film_id " +
                "WHERE LIKES.user_id = ? AND LIKE_LIST.user_id = ?";
        log.info("Получен список общих фильмов user: id={} с friend: id={}", userId, friendId);
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id"), userId, friendId);
    }

    @Override
    public Film save(Film film) {
        var simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
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
        var sqlQuery = "UPDATE FILM SET name = ?, description = ?, releaseDate = ?, duration = ?," +
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
    public boolean isNotExistsFilm(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE film_id = ?", id);
        return !filmRows.next();
    }

    private List<Long> filterPopularFilms(String withLikes, String withoutLikes, int filter) {
        var filmIdWithLikes = jdbcTemplate.queryForList(withLikes, Long.class, filter);
        if (!filmIdWithLikes.isEmpty()) {
            return filmIdWithLikes;
        } else {
            var onlyGenres = jdbcTemplate.queryForList(withoutLikes, Long.class, filter);
            if (!onlyGenres.isEmpty()) {
                return onlyGenres;
            }
        }
        return List.of();
    }

}