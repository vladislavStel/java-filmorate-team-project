package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final FeedStorage feedStorage;

    @Override
    public List<User> getAllUsers() {
        log.info("Количество зарегистрированных пользователей: {}", userStorage.findAllUsers().size());
        return userStorage.findAllUsers();
    }

    @Override
    public User getUserById(Long id) {
        if (userStorage.isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        return userStorage.findUserById(id);
    }

    @Override
    public List<User> getListFriends(Long id) {
        if (userStorage.isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        return friendsStorage.findFriends(id);
    }

    @Override
    public List<User> getListOfCommonFriends(Long id, Long otherId) {
        List<User> list = getListFriends(id);
        list.retainAll(getListFriends(otherId));
        return list;
    }

    public List<Event> getFeed(Long id) {
        if (userStorage.isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        return feedStorage.findFeed(id);
    }

    @Override
    public List<Film> getFilmRecommendations(Long id) {
        if (userStorage.isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }

        Map<Long, List<Film>> idToLikes = userStorage.findIdToFilms();
        List<Film> idLikes = idToLikes.get(id);

        return idToLikes.entrySet()
                .stream()
                .filter(e -> !Objects.equals(e.getKey(), id))
                .max(Comparator.comparingInt(e -> e.getValue().stream()
                        .filter(idLikes::contains)
                        .collect(Collectors.toSet())
                        .size()))
                .map(Map.Entry::getValue)
                .map(films -> films.stream()
                        .filter(film -> !idLikes.contains(film))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public User addUser(User user) {
        userStorage.save(user);
        return user;
    }

    @Override
    public void addNewFriend(Long userId, Long friendId) {
        if (userStorage.isNotExistsUser(userId) || userStorage.isNotExistsUser(friendId)) {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d или/и друг id=%d не найден",
                    userId, friendId));
        }
        if (userId.equals(friendId)) {
            throw new ValidationException("Ошибка валидации. Нельзя добавить себя в друзья");
        }
        friendsStorage.save(userId, friendId);
    }

    @Override
    public User updateUser(User user) {
        if (userStorage.isNotExistsUser(user.getId())) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", user.getId()));
        }
        userStorage.update(user);
        return user;
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (userStorage.isNotExistsUser(userId) || userStorage.isNotExistsUser(friendId)) {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d или/и друг id=%d не найден",
                    userId, friendId));
        }
        if (userId.equals(friendId)) {
            throw new ValidationException("Ошибка валидации. Нельзя удалить себя из друзей");
        }
        friendsStorage.delete(userId, friendId);
    }

    @Override
    public void removeUserById(Long id) {
        if (userStorage.isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь id=%d ", id));
        }
        userStorage.deleteUserById(id);
    }

}