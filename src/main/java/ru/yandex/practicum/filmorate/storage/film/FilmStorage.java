package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(Long id);

    void addLike(Long filmID, Long userID);

    void removeLike(Long filmID, Long userID);

    boolean isExistsFilm(Long id);
}