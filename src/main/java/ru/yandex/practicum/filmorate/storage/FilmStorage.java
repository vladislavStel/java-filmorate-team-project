package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    List<Long> findAllFilms();

    Film findFilmById(Long id);

    boolean isNotExistsFilm(Long id);

    void delete(Film film);

}