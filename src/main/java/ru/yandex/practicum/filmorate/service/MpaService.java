package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpa() {
        return mpaStorage.findAllMpa();
    }

    public Mpa getMpa(int id) {
        return mpaStorage.findMpaById(id);
    }

}