package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
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

    public Film getFilmByID(Long ID) {
        return filmStorage.getFilmById(ID).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Фильм не найден: id=%d", ID)));
    }

    public void addLike(Long filmID, Long userID) {
        if (filmStorage.isExistsFilm(filmID) && userStorage.isExistsUser(userID)) {
            filmStorage.addLike(filmID, userID);
        } else {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmID, userID));
        }
    }

    public void removeLike(Long filmID, Long userID) {
        if (filmStorage.isExistsFilm(filmID) && userStorage.isExistsUser(userID)) {
            filmStorage.removeLike(filmID, userID);
        } else {
            throw new ObjectNotFoundException(String.format("Фильм id=%d или/и пользователь id=%d не найден",
                    filmID, userID));
        }
    }

    public List<Film> getPopular(Integer count) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.getAllFilms());
        if (count > popularFilms.size()) {
            count = popularFilms.size();
        }
        return popularFilms.stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}