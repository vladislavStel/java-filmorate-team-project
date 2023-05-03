package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    List<Genre> findAllGenres();

    Genre findGenreById(int id);

    void saveGenresByFilm(Film film);

    void deleteGenresByFilm(Film film);

    Set<Genre> findFilmGenres(Long filmId);

}