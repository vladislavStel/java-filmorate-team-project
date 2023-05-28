package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public User getUserByID(@PathVariable("id") Long id) {
        return userService.getUserByID(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsUser(@PathVariable("id") Long id) {
        return userService.getListFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendsUser(@PathVariable("id") Long id,
                                           @PathVariable("otherId") Long otherID) {
        return userService.getListOfCommonFriends(id, otherID);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addNewFriend(@PathVariable("id") Long userID,
                             @PathVariable("friendId") Long friendID) {
        userService.addNewFriend(userID, friendID);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userID,
                             @PathVariable("friendId") Long friendID) {
        userService.removeFriend(userID, friendID);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") Long userID) {
        userService.removeUserById(userID);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable("id") Long id) {
        return userService.getFeed(id);
    }

}