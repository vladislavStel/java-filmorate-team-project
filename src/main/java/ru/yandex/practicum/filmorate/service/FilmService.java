package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public List<Film> getAllFilms() {
        return filmStorage.findAllFilms().stream().map(this::getFilmByID).collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        filmStorage.save(film);
        genreStorage.saveGenresByFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.update(film);
        genreStorage.deleteGenresByFilm(film);
        genreStorage.saveGenresByFilm(film);
        return getFilmByID(film.getId());
    }

    public Film getFilmByID(Long id) {
        if (filmStorage.isNotExistsFilm(id)) {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", id));
        }
        Film film = filmStorage.findFilmById(id);
        int mpaId = film.getMpa().getId();
        film.setMpa(mpaStorage.findMpaById(mpaId));
        film.setGenres(genreStorage.findFilmGenres(id));
        return film;
    }

    public void addLike(Long filmID, Long userID) {
        if (filmStorage.isNotExistsFilm(filmID) || userStorage.isNotExistsUser(userID)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmID, userID));
        }
        likesStorage.saveLike(filmID, userID);
    }

    public void removeLike(Long filmID, Long userID) {
        if (filmStorage.isNotExistsFilm(filmID) || userStorage.isNotExistsUser(userID)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmID, userID));
        }
        likesStorage.deleteLike(filmID, userID);
    }

    public List<Film> getPopular(Long count) {
        List<Long> topFilms = likesStorage.findPopular(count);
        if (!topFilms.isEmpty()) {
            return topFilms.stream().map(this::getFilmByID).collect(Collectors.toList());
        }
        return getAllFilms().stream().limit(count).collect(Collectors.toList());
    }

}