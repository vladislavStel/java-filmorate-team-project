package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {

    void save(Long userId, Long friendId);

    void delete(Long userId, Long friendId);

    List<User> findFriends(Long id);

}