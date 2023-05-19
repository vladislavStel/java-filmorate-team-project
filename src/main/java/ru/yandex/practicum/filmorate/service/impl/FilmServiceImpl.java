package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.findAllFilms().stream().map(this::getFilmByID).collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        filmStorage.save(film);
        genreStorage.saveGenresByFilm(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmStorage.update(film);
        genreStorage.deleteGenresByFilm(film);
        genreStorage.saveGenresByFilm(film);
        return getFilmByID(film.getId());
    }

    @Override
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

    @Override
    public void addLike(Long filmID, Long userID) {
        if (filmStorage.isNotExistsFilm(filmID) || userStorage.isNotExistsUser(userID)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmID, userID));
        }
        likesStorage.saveLike(filmID, userID);
    }

    @Override
    public void removeLike(Long filmID, Long userID) {
        if (filmStorage.isNotExistsFilm(filmID) || userStorage.isNotExistsUser(userID)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmID, userID));
        }
        likesStorage.deleteLike(filmID, userID);
    }

    @Override
    public List<Film> getPopular(Long count) {
        List<Long> topFilms = likesStorage.findPopular(count);
        if (!topFilms.isEmpty()) {
            return topFilms.stream().map(this::getFilmByID).collect(Collectors.toList());
        }
        return getAllFilms().stream().limit(count).collect(Collectors.toList());
    }

}