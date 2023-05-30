package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("{id}")
    public Film getFilmByID(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") @Positive int count,
                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int genreId,
                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsSortByDirector(@Valid @PathVariable("directorId") int directorId,
                                             @RequestParam(value = "sortBy", required = false) String sortBy) {
        return filmService.getFilmsSorted(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getFilmsByDirectorAndTitle(@RequestParam(name = "query")
                                                 @NotBlank String query,
                                                 @RequestParam(name = "by", required = false) String by) {
        return filmService.getFilmsByDirectorAndTitle(query, by);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilmsWithFriend(@RequestParam @Positive Long userId,
                                               @RequestParam @Positive Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeByFilm(@PathVariable("id") Long filmID,
                              @PathVariable("userId") Long userID) {
        filmService.addLike(filmID, userID);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeByFilm(@PathVariable("id") Long filmID,
                                 @PathVariable("userId") Long userID) {
        filmService.removeLike(filmID, userID);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable("id") Long filmID) {
        filmService.removeFilmById(filmID);
    }

}