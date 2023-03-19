package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final IdCounter idCounter = new IdCounter();
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Количество зарегистрированных пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getId() == null || user.getId() == 0) {
            user.setId(idCounter.getIdCounter());
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Неверный формат Login");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен объект: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null || user.getId() == 0) {
            user.setId(idCounter.getIdCounter());
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Неверный формат Login");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            users.put(user.getId(), user);
            log.info("Добавлен объект: {}", user);
            return user;
        } else {
            throw new ValidationException("Пользователь с id: " + user.getId() + " отсутствует в базе!");
        }
    }
}