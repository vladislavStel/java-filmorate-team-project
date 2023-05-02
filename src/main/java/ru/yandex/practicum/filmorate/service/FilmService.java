package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.LinkedHashSet;
import java.util.List;

@Service
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;

    public FilmService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       LikesStorage likesStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        filmStorage.save(film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.update(film);
        Film filmSetGenres = filmStorage.findFilmById(film.getId());
        if (film.getGenres() == null) {
            filmSetGenres.setGenres(null);
        } else if (film.getGenres().isEmpty()) {
            filmSetGenres.setGenres(new LinkedHashSet<>());
        }
        return filmSetGenres;
    }

    public Film getFilmByID(Long id) {
        if (filmStorage.isNotExistsFilm(id)) {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", id));
        }
        return filmStorage.findFilmById(id);
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
        return likesStorage.findPopular(count);
    }

}