package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    List<User> findAllUsers();

    User findUserById(Long id);

    Set<Long> findLikeListByUserId(Long id);

    Set<Long> findLikeList();

    User save(User user);

    User update(User user);

    void delete(User user);

    void deleteUserById(Long id);

    boolean isNotExistsUser(Long id);

}