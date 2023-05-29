package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();

    User getUserByID(Long id);

    List<User> getListFriends(Long id);

    List<User> getListOfCommonFriends(Long id, Long otherId);

    List<Event> getFeed(Long userId);

    Set<Film> getFilmRecommendations(Long id);

    User addUser(User user);

    void addNewFriend(Long userId, Long friendId);

    User updateUser(User user);

    void removeFriend(Long userId, Long friendId);

    void removeUserById(Long id);

}