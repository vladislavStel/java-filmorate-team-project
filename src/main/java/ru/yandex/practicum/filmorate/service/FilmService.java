package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    List<Film> getPopularFilms(int count, int genreId, int year);

    List<Film> getFilmsSorted(int directorId, String sortBy);

    List<Long> getFilmsByDirectorSorted(int directorId, String sortBy);

    List<Film> getFilmsByDirectorAndTitle(String query, String by);

    List<Film> getCommonFilms(Long userId, Long friendId);

    Film addFilm(Film film);

    void addLike(Long filmId, Long userId);

    Film updateFilm(Film film);

    void removeLike(Long filmId, Long userId);

    void removeFilmById(Long filmId);

}