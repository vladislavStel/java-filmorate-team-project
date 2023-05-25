package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    List<Long> findAllFilms();

    Film findFilmById(Long id);

    void delete(Film film);

    void deleteFilmById(Long id);

    List<Long> findFilmsByDirectorSorted(int directorId, String sortBy);

    List<Long> findFilmsByDirectorSortedByYear(int directorId);

    List<Long> findFilmsByDirectorSortedByLikes(int directorId);

    List<Long> findFilmsByDirector(int directorId);

    boolean isNotExistsFilm(Long id);

    List<Film> getPopularFilmSortedByGenreAndYear(Long count, Long genreId, Integer year);

    List<Film> getPopularFilmSortedByGenre(Long count, Long genreId);

    List<Film> getPopularFilmSortedByYear(Long count, Integer year);

    List<Film> getPopular(Long count);

}