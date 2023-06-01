package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    List<User> findAllUsers();

    User findUserById(Long id);

    Map<Long, List<Film>> findIdToFilms();

    User save(User user);

    User update(User user);

    void delete(User user);

    void deleteUserById(Long id);

    boolean isNotExistsUser(Long id);

}