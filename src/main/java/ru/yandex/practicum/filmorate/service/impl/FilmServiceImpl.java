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
        return filmStorage.findAllFilms().stream().map(this::getFilmByID).collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        filmStorage.save(film);
        genreStorage.saveGenresByFilm(film);
        directorStorage.saveDirectorByFilm(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmStorage.update(film);
        genreStorage.deleteGenresByFilm(film);
        genreStorage.saveGenresByFilm(film);
        directorStorage.deleteDirectorByFilm(film);
        directorStorage.saveDirectorByFilm(film);
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
        film.setDirectors(directorStorage.findFilmDirectors(id));
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
    public void removeFilmById(Long filmId) {
        if (filmStorage.isNotExistsFilm(filmId)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d не найден", filmId));
        }
        filmStorage.deleteFilmById(filmId);
    }

    @Override
    public List<Film> getPopular(Long count, int genreId, Integer year) {
        if (genreId != 0 && year != 0) {
            return filmStorage.findPopularFilmSortedByGenreAndYear(count, genreId, year);
        }
        if (genreId != 0 && year == 0) {
            return filmStorage.findPopularFilmSortedByGenre(count, genreId);
        }
        if (genreId == 0 && year != 0) {
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

        return filmStorage
                .findFilmsByDirectorSorted(directorId, sortBy)
                .stream()
                .map(this::getFilmByID)
                .collect(Collectors.toList());
    }

}