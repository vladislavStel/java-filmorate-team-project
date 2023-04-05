package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.add(user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.update(user);
        return user;
    }

    public User getUserByID(Long id) {
        return userStorage.getUserById(id).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id)));
    }

    public void addNewFriend(Long userID, Long friendID) {
        if (userStorage.isExistsUser(userID) && userStorage.isExistsUser(friendID)) {
            userStorage.addFriend(userID, friendID);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d или/и друг id=%d не найден",
                    userID, friendID));
        }
    }

    public void removeFriend(Long userID, Long friendID) {
        if (userStorage.isExistsUser(userID) && userStorage.isExistsUser(friendID)) {
            userStorage.removeFriend(userID, friendID);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d или/и друг id=%d не найден",
                    userID, friendID));
        }
    }

    public List<User> getListFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public List<User> getListOfCommonFriends(Long id, Long otherID) {
        List<User> list = userStorage.getFriends(id);
        list.retainAll(userStorage.getFriends(otherID));
        return list;
    }
}