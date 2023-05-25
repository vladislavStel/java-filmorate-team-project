package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {

    List<Director> findAllDirectors();

    Director findDirectorById(int id);

    void saveDirectorByFilm(Film film);

    void deleteDirectorByFilm(Film film);

    Set<Director> findFilmDirectors(Long filmId);

    Director save(Director director);

    Director update(Director director);

    void delete(int id);

    boolean isNotExistsDirector(int id);

}