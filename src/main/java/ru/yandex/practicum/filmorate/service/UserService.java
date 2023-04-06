package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.annotation.NameValidator;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final NameValidator nameValidator;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        user.setName(nameValidator.validateName(user.getName(), user.getLogin()));
        userStorage.add(user);
        return user;
    }

    public User updateUser(User user) {
        user.setName(nameValidator.validateName(user.getName(), user.getLogin()));
        userStorage.update(user);
        return user;
    }

    public User getUserByID(Long id) {
        if (userStorage.isExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        return userStorage.getUserById(id);
    }

    public void addNewFriend(Long userID, Long friendID) {
        if (userStorage.isExistsUser(userID) || userStorage.isExistsUser(friendID)) {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d или/и друг id=%d не найден",
                    userID, friendID));
        }
        userStorage.addFriend(userID, friendID);
    }

    public void removeFriend(Long userID, Long friendID) {
        if (userStorage.isExistsUser(userID) || userStorage.isExistsUser(friendID)) {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d или/и друг id=%d не найден",
                    userID, friendID));
        }
        userStorage.removeFriend(userID, friendID);
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