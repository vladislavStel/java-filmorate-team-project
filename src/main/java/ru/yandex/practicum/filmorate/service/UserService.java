package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    List<User> getListFriends(Long id);

    List<User> getListOfCommonFriends(Long id, Long otherId);

    List<Event> getFeed(Long userId);

    List<Film> getFilmRecommendations(Long id);

    User addUser(User user);

    void addNewFriend(Long userId, Long friendId);

    User updateUser(User user);

    void removeFriend(Long userId, Long friendId);

    void removeUserById(Long id);

}