package ru.yandex.practicum.filmorate.exception;

public class IncorrectParameterException extends RuntimeException {
    private final Integer count;

    public IncorrectParameterException(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }
}