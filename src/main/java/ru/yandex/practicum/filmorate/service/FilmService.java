package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmByID(Long id);

    void addLike(Long filmID, Long userID);

    void removeLike(Long filmID, Long userID);

    void removeFilmById(Long filmId);

    List<Film> getPopular(Long count, int genreId, Integer year);

    List<Film> getFilmsSorted(int directorId, String sortBy);

    List<Long> getFilmsByDirectorSorted(int directorId, String sortBy);

    List<Film> getFilmsByDirectorAndTitle(String query, String by);

    List<Film> getCommonFilms(Long userId, Long friendId);

}