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
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (isExistsFilm(film.getId())) {
            films.put(film.getId(), film);
            log.info("Данные фильма обновлены: id={}", film.getId());
            return film;
        } else {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", film.getId()));
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Количество добавленных фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        if (isExistsFilm(id)) {
            return Optional.of(films.get(id));
        } else {
            throw new ObjectNotFoundException(String.format("Фильм не найден: id=%d", id));
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
    public boolean isExistsFilm(Long id) {
        return films.containsKey(id);
    }

    private Long generateId() {
        return ++filmID;
    }
}