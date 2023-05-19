package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUserByID(Long id);

    void addNewFriend(Long userID, Long friendID);

    void removeFriend(Long userID, Long friendID);

    List<User> getListFriends(Long id);

    List<User> getListOfCommonFriends(Long id, Long otherID);

}