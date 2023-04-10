package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long userID = 0L;

    @Override
    public User add(User user) {
        user.setId(generateID());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (isNotExistsUser(user.getId())) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", user.getId()));
        }
        users.put(user.getId(), user);
        log.info("Данные пользователя обновлены: id={}", user.getId());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Количество зарегистрированных пользователей: {}", users.size());
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public void addFriend(Long userID, Long friendID) {
        if (userID.equals(friendID)) {
            throw new ValidationException("Ошибка валидации. Нельзя добавить себя в друзья");
        }
        users.get(userID).getFriends().add(friendID);
        users.get(friendID).getFriends().add(userID);
    }

    @Override
    public void removeFriend(Long userID, Long friendID) {
        if (userID.equals(friendID)) {
            throw new ValidationException("Ошибка валидации. Нельзя удалить себя из друзей");
        }
        users.get(userID).getFriends().remove(friendID);
        users.get(friendID).getFriends().remove(userID);
    }

    @Override
    public List<User> getFriends(Long id) {
        if (isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        return users.get(id).getFriends().stream().map(users::get).collect(Collectors.toList());
    }

    @Override
    public boolean isNotExistsUser(Long id) {
        return !users.containsKey(id);
    }

    private Long generateID() {
        return ++userID;
    }
}