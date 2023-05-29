package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final FilmService filmService;
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final FeedStorage feedStorage;

    @Override
    public List<User> getAllUsers() {
        log.info("Количество зарегистрированных пользователей: {}", userStorage.findAllUsers().size());
        return userStorage.findAllUsers();
    }

    @Override
    public User getUserByID(Long id) {
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
    public Set<Film> getFilmRecommendations(Long id) {
        if (userStorage.isNotExistsUser(id)) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден: id=%d", id));
        }
        Set<Film> result = new HashSet<>();
        Set<Long> allUsersId = getAllUsers().stream().map(User::getId).collect(Collectors.toSet());

        long intersectionAmount = 0;
        long otherUserInterception = -1;
        Set<Long> filmsIdRecommended = null;

        for (Long otherUserId : allUsersId) {
            if (!id.equals(otherUserId)) {
                Set<Long> likesListByOtherUser = userStorage.findLikeListByUserId(otherUserId);
                Set<Long> intersectionList = new HashSet<>(userStorage.findLikeListByUserId(id));
                intersectionList.retainAll(likesListByOtherUser);
                if (intersectionList.size() > intersectionAmount) {
                    intersectionAmount = intersectionList.size();
                    otherUserInterception = otherUserId;
                    intersectionList.forEach(likesListByOtherUser::remove);
                    filmsIdRecommended = likesListByOtherUser;
                }
            }
        }
        if (otherUserInterception != -1 || filmsIdRecommended != null) {
            filmsIdRecommended.forEach(filmId -> result.add(filmService.getFilmById(filmId)));
        }
        return result;
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