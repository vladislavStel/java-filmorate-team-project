package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long userID = 0L;

    @Override
    public User add(User user) {
        user.setID(generateID());
        users.put(user.getID(), user);
        log.info("Добавлен новый пользователь: id={}", user.getID());
        return user;
    }

    @Override
    public User update(User user) {
        if (isExistsUser(user.getID())) {
            users.put(user.getID(), user);
            log.info("Данные пользователя обновлены: id={}", user.getID());
            return user;
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", user.getID()));
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Количество зарегистрированных пользователей: {}", users.size());
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long ID) {
        if (isExistsUser(ID)) {
            return Optional.of(users.get(ID));
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", ID));
        }
    }

    @Override
    public void addFriend(Long userID, Long friendID) {
        if (!userID.equals(friendID)) {
            users.get(userID).getFriends().add(friendID);
            users.get(friendID).getFriends().add(userID);
        } else {
            throw new ValidationException("Ошибка валидации");
        }
    }

    @Override
    public void removeFriend(Long userID, Long friendID) {
        if (!userID.equals(friendID)) {
            users.get(userID).getFriends().remove(friendID);
            users.get(friendID).getFriends().remove(userID);
        } else {
            throw new ValidationException("Ошибка валидации");
        }
    }

    @Override
    public List<User> getFriends(Long ID) {
        if (isExistsUser(ID)) {
            return users.get(ID).getFriends().stream().map(users::get).collect(Collectors.toList());
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", ID));
        }
    }

    @Override
    public boolean isExistsUser(Long ID) {
        return users.containsKey(ID);
    }

    private Long generateID() {
        return ++userID;
    }
}