package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {

    void saveLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

}