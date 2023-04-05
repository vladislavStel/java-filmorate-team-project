package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping                                                 // получаем список всех users
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")                                        // получить user по ID
    public User getUserByID(@PathVariable("id") Long id) {
        return userService.getUserByID(id);
    }

    @GetMapping("/{id}/friends")                                // получить список friends по ID user
    public List<User> getFriendsUser(@PathVariable("id") Long id) {
        return userService.getListFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")               // получить список общих friends с otherUser
    public List<User> getCommonFriendsUser(@PathVariable("id") Long id,
                                           @PathVariable("otherId") Long otherID) {
        return userService.getListOfCommonFriends(id, otherID);
    }

    @PostMapping                                                // создать user
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping                                                 // обновить данные user
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")                     // добавить друга friendID к user
    public void addNewFriend(@PathVariable("id") Long userID,
                             @PathVariable("friendId") Long friendID) {
        userService.addNewFriend(userID, friendID);
    }

    @DeleteMapping("/{id}/friends/{friendId}")                  // удалить друга friendID у user
    public void deleteFriend(@PathVariable("id") Long userID,
                             @PathVariable("friendId") Long friendID) {
        userService.removeFriend(userID, friendID);
    }
}