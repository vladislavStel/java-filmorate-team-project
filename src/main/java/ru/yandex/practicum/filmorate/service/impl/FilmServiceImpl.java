package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final MpaStorage mpaStorage;

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.findAllFilms().stream().map(this::getFilmById).collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Long id) {
        if (filmStorage.isNotExistsFilm(id)) {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", id));
        }
        var film = filmStorage.findFilmById(id);
        var mpaId = film.getMpa().getId();
        film.setMpa(mpaStorage.findMpaById(mpaId));
        film.setGenres(genreStorage.findFilmGenres(id));
        film.setDirectors(directorStorage.findFilmDirectors(id));
        return film;
    }

    @Override
    public List<Film> getPopular(Long count, int genreId, Integer year) {
        if (genreId != 0 && year != 0) {
            return filmStorage.findPopularFilmSortedByGenreAndYear(count, genreId, year);
        }
        if (genreId != 0) {
            return filmStorage.findPopularFilmSortedByGenre(count, genreId);
        }
        if (year != 0) {
            return filmStorage.findPopularFilmSortedByYear(count, year);
        }
        if (year > Year.now().getValue()) {
            throw new ValidationException("Выбраный год не был найден");
        }

        return filmStorage.findPopular(count);
    }

    @Override
    public List<Film> getFilmsSorted(int directorId, String sortBy) {

        if (directorStorage.isNotExistsDirector(directorId)) {
            throw new ObjectNotFoundException(String.format("Не найден режиссер: id=%d", directorId));
        }

        return getFilmsByDirectorSorted(directorId, sortBy)
                .stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getFilmsByDirectorSorted(int directorId, String sortBy) {
        switch (sortBy) {
            case ("year"):
                return filmStorage.findFilmsByDirectorSortedByYear(directorId);
            case ("likes"):
                return filmStorage.findFilmsByDirectorSortedByLikes(directorId);
            default:
                return filmStorage.findFilmsByDirectorById(directorId);
        }
    }

    @Override
    public List<Film> getFilmsByDirectorAndTitle(String query, String by) {

        List<Film> listFilms = new ArrayList<>();
        if (by.contains("director")) {
            listFilms.addAll(filmStorage.findFilmsByDirector(query));
        }
        if (by.contains("title")) {
            listFilms.addAll(filmStorage.findFilmsByTitle(query));
        }
        return listFilms;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.findCommonFilmsWithFriend(userId, friendId)
                .stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        filmStorage.save(film);
        genreStorage.saveGenresByFilm(film);
        directorStorage.saveDirectorByFilm(film);
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
    public Film updateFilm(Film film) {
        filmStorage.update(film);
        genreStorage.deleteGenresByFilm(film);
        genreStorage.saveGenresByFilm(film);
        directorStorage.deleteDirectorByFilm(film);
        directorStorage.saveDirectorByFilm(film);
        return getFilmById(film.getId());
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
    public void removeFilmById(Long filmId) {
        if (filmStorage.isNotExistsFilm(filmId)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d не найден", filmId));
        }
        filmStorage.deleteFilmById(filmId);
    }

}