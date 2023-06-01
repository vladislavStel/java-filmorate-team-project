package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {

    List<Director> findAllDirectors();

    Director findDirectorById(int id);

    Set<Director> findFilmDirectors(Long filmId);

    void saveDirectorByFilm(Film film);

    Director save(Director director);

    Director update(Director director);

    void deleteDirectorByFilm(Film film);

    void delete(int id);

    boolean isNotExistsDirector(int id);

}