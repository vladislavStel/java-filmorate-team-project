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

    List<User> getListOfCommonFriends(Long id, Long otherID);

    List<Event> getFeed(Long userId);

    Set<Film> getFilmRecommendations(Long Id);

    User addUser(User user);

    void addNewFriend(Long userID, Long friendID);

    User updateUser(User user);

    void removeFriend(Long userID, Long friendID);

    void removeUserById(Long id);

}