package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        filmStorage.add(film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.update(film);
        return film;
    }

    public Film getFilmByID(Long id) {
        if (filmStorage.isNotExistsFilm(id)) {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", id));
        }
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long filmID, Long userID) {
        if (filmStorage.isNotExistsFilm(filmID) || userStorage.isNotExistsUser(userID)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmID, userID));
        }
        filmStorage.addLike(filmID, userID);
    }

    public void removeLike(Long filmID, Long userID) {
        if (filmStorage.isNotExistsFilm(filmID) || userStorage.isNotExistsUser(userID)) {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmID, userID));
        }
        filmStorage.removeLike(filmID, userID);
    }

    public List<Film> getPopular(Integer count) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.getAllFilms());
        return popularFilms.stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}