package ru.yandex.practicum.filmorate.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NameValidator {
    public String validateName(String name, String login) {
        if (name == null || name.isBlank()) {
            log.info("В качестве имени использован логин.");
            return login;
        }
        return name;
    }
}