package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final IdCounter idCounter = new IdCounter();
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAllFilms() {
        log.info("Количество добавленных фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null || film.getId() == 0) {
            film.setId(idCounter.getIdCounter());
        }
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            throw new ValidationException("Неверная дата релиза. Кино еще не изобрели!");
        }
        films.put(film.getId(), film);
        log.info("Добавлен объект: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null || film.getId() == 0) {
            film.setId(idCounter.getIdCounter());
        }
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            throw new ValidationException("Неверная дата релиза. Кино еще не изобрели!");
        }
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            films.put(film.getId(), film);
            log.info("Добавлен объект: {}", film);
            return film;
        } else {
            throw new ValidationException("Фильм с id: " + film.getId() + " отсутствует в базе!");
        }
    }
}