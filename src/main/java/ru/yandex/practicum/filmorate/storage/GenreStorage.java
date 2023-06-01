package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    List<Genre> findAllGenres();

    Genre findGenreById(int id);

    Set<Genre> findFilmGenres(Long filmId);

    void saveGenresByFilm(Film film);

    void deleteGenresByFilm(Film film);

}