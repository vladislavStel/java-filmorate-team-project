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
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId > 0 || year > 0) {
            return getPopularFilmsSorted(count, genreId, year);
        } else if (genreId == 0 && year == 0) {
            var topFilmsId = filmStorage.findPopularFilms(count);
            if (!topFilmsId.isEmpty()) {
                return topFilmsId.stream().map(this::getFilmById).collect(Collectors.toList());
            }
        }
        return filmStorage.findAllFilms().stream().map(this::getFilmById).limit(count).collect(Collectors.toList());
    }

    private List<Film> getPopularFilmsSorted(int count, int genreId, int year) {
        List<Long> popularSortedFilmId;
        if (year > 0) {
            if (year > Year.now().getValue()) {
                throw new ValidationException("Некорректный год сортировки");
            }
            popularSortedFilmId = filmStorage.findPopularFilmsSortedByYear(count, year);
            if (genreId > 0) {
                var genre = genreStorage.findGenreById(genreId);
                popularSortedFilmId.stream()
                    .map(this::getFilmById)
                    .filter((film) -> film.getGenres().contains(genre))
                    .collect(Collectors.toList());
            }
        } else {
            popularSortedFilmId = filmStorage.findPopularFilmsSortedByGenre(genreId);
        }

        return popularSortedFilmId.stream()
                .limit(count)
                .map(this::getFilmById)
                .collect(Collectors.toList());
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
    public void addLike(Long filmId, Long userId) {
        if (filmStorage.isNotExistsFilm(filmId) || userStorage.isNotExistsUser(userId)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmId, userId));
        }
        likesStorage.saveLike(filmId, userId);
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
    public void removeLike(Long filmId, Long userId) {
        if (filmStorage.isNotExistsFilm(filmId) || userStorage.isNotExistsUser(userId)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmId, userId));
        }
        likesStorage.deleteLike(filmId, userId);
    }

    @Override
    public void removeFilmById(Long filmId) {
        if (filmStorage.isNotExistsFilm(filmId)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d не найден", filmId));
        }
        filmStorage.deleteFilmById(filmId);
    }

}