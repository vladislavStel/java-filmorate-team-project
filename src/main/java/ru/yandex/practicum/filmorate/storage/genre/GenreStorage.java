package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;

public interface GenreStorage {

    List<Genre> findAllGenres();

    Genre findGenreById(int id);

    void saveGenresByFilm(Film film);

    void deleteGenresByFilm(Film film);

    HashSet<Genre> findFilmGenres(Long filmId);

}