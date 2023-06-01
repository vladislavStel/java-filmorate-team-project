package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public List<Director> getAllDirectors() {
        return directorStorage.findAllDirectors();
    }

    @Override
    public Director getDirectorById(int id) {
        return directorStorage.findDirectorById(id);
    }

    @Override
    public Director addDirector(Director director) {
        return directorStorage.save(director);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorStorage.update(director);
    }

    @Override
    public void removeDirector(int id) {
        directorStorage.delete(id);
    }

}