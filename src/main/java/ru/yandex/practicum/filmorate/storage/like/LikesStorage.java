package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {

    void saveLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> findPopular(Long count);

}