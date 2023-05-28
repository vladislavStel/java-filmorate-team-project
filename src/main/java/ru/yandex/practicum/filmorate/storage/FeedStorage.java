package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {

    void saveEvent(Long userId, String eventType, String operation, Long entityId);

    List<Event> findFeed(Long id);
}
