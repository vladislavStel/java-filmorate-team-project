package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public List<User> getAllUsers() {
        log.info("Количество зарегистрированных пользователей: {}", userStorage.findAllUsers().size());
        return userStorage.findAllUsers();
    }

    public User addUser(User user) {
        userStorage.save(user);
        return user;
    }

    public User updateUser(User user) {
        if (userStorage.isNotExistsUser(user.getId())) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", user.getId()));
        }
        userStorage.update(user);
        return user;
    }

    public User getUserByID(Long id) {
        if (userStorage.isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        return userStorage.findUserById(id);
    }

    public void addNewFriend(Long userID, Long friendID) {
        if (userStorage.isNotExistsUser(userID) || userStorage.isNotExistsUser(friendID)) {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d или/и друг id=%d не найден",
                    userID, friendID));
        }
        if (userID.equals(friendID)) {
            throw new ValidationException("Ошибка валидации. Нельзя добавить себя в друзья");
        }
        friendsStorage.save(userID, friendID);
    }

    public void removeFriend(Long userID, Long friendID) {
        if (userStorage.isNotExistsUser(userID) || userStorage.isNotExistsUser(friendID)) {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d или/и друг id=%d не найден",
                    userID, friendID));
        }
        if (userID.equals(friendID)) {
            throw new ValidationException("Ошибка валидации. Нельзя удалить себя из друзей");
        }
        friendsStorage.delete(userID, friendID);
    }

    public List<User> getListFriends(Long id) {
        if (userStorage.isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        return friendsStorage.findFriends(id);
    }

    public List<User> getListOfCommonFriends(Long id, Long otherID) {
        List<User> list = getListFriends(id);
        list.retainAll(getListFriends(otherID));
        return list;
    }

}