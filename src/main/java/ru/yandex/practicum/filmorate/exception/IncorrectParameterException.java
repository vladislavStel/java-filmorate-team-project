package ru.yandex.practicum.filmorate.exception;

public class IncorrectParameterException extends RuntimeException {
    private final Long count;

    public IncorrectParameterException(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

}