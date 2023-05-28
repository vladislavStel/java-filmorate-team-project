package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserByID(Long id);

    List<User> getListFriends(Long id);

    List<User> getListOfCommonFriends(Long id, Long otherID);

    List<Event> getFeed(Long userId);

    User addUser(User user);

    void addNewFriend(Long userID, Long friendID);

    User updateUser(User user);

    void removeFriend(Long userID, Long friendID);

    void removeUserById(Long id);

}