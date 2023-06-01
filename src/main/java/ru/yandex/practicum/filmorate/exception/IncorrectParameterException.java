package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class IncorrectParameterException extends RuntimeException {
    private final Long count;

    public IncorrectParameterException(Long count) {
        this.count = count;
    }

}