package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping                                                 // получаем список всех films
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("{id}")                                        // получить film по ID
    public Film getFilmByID(@PathVariable("id") Long id) {
        return filmService.getFilmByID(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count < 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.getPopular(count);
    }

    @PostMapping                                                // создать film
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping                                                 // обновить данные film
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")                          // добавить like к film от userID
    public void addLikeByFilm(@PathVariable("id") Long filmID,
                              @PathVariable("userId") Long userID) {
        filmService.addLike(filmID, userID);
    }

    @DeleteMapping("/{id}/like/{userId}")                        // удалить like у film от userID
    public void deleteLikeByFilm(@PathVariable("id") Long filmID,
                                 @PathVariable("userId") Long userID) {
        filmService.removeLike(filmID, userID);
    }
}