package ru.yandex.practicum.filmorate.storage.like;

import java.util.List;

public interface LikesStorage {

    void saveLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Long> findPopular(Long count);

}