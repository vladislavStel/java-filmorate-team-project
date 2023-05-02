package ru.yandex.practicum.filmorate.storage.like;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public void saveLike(Long filmId, Long userId) {
        jdbcTemplate.update("INSERT INTO LIKE_LIST (film_id, user_id) VALUES (? , ?)", filmId, userId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        jdbcTemplate.update("DELETE FROM LIKE_LIST WHERE user_id = ? AND film_id = ?", userId, filmId);
        log.info("Пользователь id={} удалил лайк у фильма id={}", userId, filmId);
    }

    @Override
    public List<Film> findPopular(Long count) {
        return jdbcTemplate.query("SELECT FILM.film_id, name, description, releaseDate, duration, mpa_id , " +
            "COUNT(L.user_id) as rating FROM FILM LEFT JOIN LIKE_LIST L ON FILM.film_id = L.film_id " +
            "GROUP BY FILM.film_id " +
            "ORDER BY rating DESC LIMIT ?", (rs, rowNum) -> new Film(
                    rs.getLong("film_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("releaseDate").toLocalDate(),
                    rs.getInt("duration"),
                    genreStorage.findFilmGenres(rs.getLong("film_id")),
                    mpaStorage.findMpaById(rs.getInt("mpa_id")),
                    rs.getLong("rating")
                ), count);
    }

}