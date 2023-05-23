package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User save(User user);

    User update(User user);

    List<User> findAllUsers();

    User findUserById(Long id);

    boolean isNotExistsUser(Long id);

    void delete(User user);

    void deleteUserById(Long id);

}