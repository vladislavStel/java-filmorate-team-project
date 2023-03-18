package ru.yandex.practicum.filmorate.controller;

public class IdCounter {
    private Long idCounter = 0L;

    public Long getIdCounter() {
        return ++idCounter;
    }
}