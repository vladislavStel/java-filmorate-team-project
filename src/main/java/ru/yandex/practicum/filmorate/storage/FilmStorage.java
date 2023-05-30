package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Long> findAllFilms();

    Film findFilmById(Long id);

    List<Long> findFilmsByDirectorSortedByYear(int directorId);

    List<Long> findFilmsByDirectorSortedByLikes(int directorId);

    List<Long> findFilmsByDirectorById(int directorId);

    List<Long> findCommonFilmsWithFriend(Long userId, Long friendId);

    List<Long> findPopularFilmsSortedByGenre(int genreId);

    List<Long> findPopularFilmsSortedByYear(int count, int year);

    List<Long> findPopularFilms(int count);

    List<Film> findFilmsByTitle(String query);

    List<Film> findFilmsByDirector(String query);

    Film save(Film film);

    Film update(Film film);

    void delete(Film film);

    void deleteFilmById(Long id);

    boolean isNotExistsFilm(Long id);

}