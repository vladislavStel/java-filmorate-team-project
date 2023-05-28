package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms();

    Film getFilmByID(Long id);

    List<Film> getPopular(Long count, int genreId, Integer year);

    List<Film> getFilmsSorted(int directorId, String sortBy);

    List<Long> getFilmsByDirectorSorted(int directorId, String sortBy);

    List<Film> getFilmsByDirectorAndTitle(String query, String by);

    List<Film> getCommonFilms(Long userId, Long friendId);

    Film addFilm(Film film);

    void addLike(Long filmID, Long userID);

    Film updateFilm(Film film);

    void removeLike(Long filmID, Long userID);

    void removeFilmById(Long filmId);

}