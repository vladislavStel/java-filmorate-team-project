package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long filmID = 0L;

    @Override
    public Film add(Film film) {
        film.setID(generateId());
        films.put(film.getID(), film);
        log.info("Добавлен новый фильм: id={}", film.getID());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (isExistsFilm(film.getID())) {
            films.put(film.getID(), film);
            log.info("Данные фильма обновлены: id={}", film.getID());
            return film;
        } else {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", film.getID()));
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Количество добавленных фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Long ID) {
        if (isExistsFilm(ID)) {
            return Optional.of(films.get(ID));
        } else {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", ID));
        }
    }

    @Override
    public void addLike(Long filmID, Long userID) {
        films.get(filmID).getLikes().add(userID);
    }

    @Override
    public void removeLike(Long filmID, Long userID) {
        films.get(filmID).getLikes().remove(userID);
    }

    @Override
    public boolean isExistsFilm(Long ID) {
        return films.containsKey(ID);
    }

    private Long generateId() {
        return ++filmID;
    }
}