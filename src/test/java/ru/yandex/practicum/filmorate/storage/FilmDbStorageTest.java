package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    private static Film film1;
    private static Film film2;
    private static Film filmNotFind;

    @AfterEach
    void AfterEach() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, "FILM");
        jdbcTemplate.update("ALTER TABLE FILM ALTER COLUMN film_id RESTART WITH 1");

    }

    @BeforeAll
    public static void init() {

        film1 = Film.builder()
                .name("Name_film")
                .description("Description film")
                .releaseDate(LocalDate.of(2000, 5, 25))
                .duration(100)
                .build();
        film1.setMpa(new Mpa(1));

        film2 = Film.builder()
                .name("Name_film2")
                .description("Description film2")
                .releaseDate(LocalDate.of(1990, 10, 2))
                .duration(50)
                .build();
        film2.setMpa(new Mpa(4));

        filmNotFind = Film.builder()
                .name("TestName")
                .description("TestDescription")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        filmNotFind.setId(9999L);
        filmNotFind.setMpa(new Mpa(1));

    }

    @Test
    void testSaveFilm() {

        filmDbStorage.save(film1);

        Film fetchedFilm = filmDbStorage.findFilmById(film1.getId());

        assertEquals(1, fetchedFilm.getId());
        assertEquals(film1.getName(), fetchedFilm.getName());

    }

    @Test
    void testUpdateFilm() {

        Film film1updated = Film.builder()
                .name("Name_film1_update")
                .description("Description film1_update")
                .releaseDate(LocalDate.of(2100, 5, 25))
                .duration(200)
                .build();
        film1updated.setId(1L);
        film1updated.setMpa(new Mpa(2));

        filmDbStorage.save(film1);
        filmDbStorage.update(film1updated);

        Film fetchedFilm = filmDbStorage.findFilmById(film1updated.getId());

        assertEquals(film1.getId(), film1updated.getId());
        assertEquals(film1updated.getName(), fetchedFilm.getName());
        assertEquals(film1updated.getDescription(), fetchedFilm.getDescription());
        assertThrows(ObjectNotFoundException.class, () -> filmDbStorage.update(filmNotFind));

    }

    @Test
    void testFindAllFilms() {

        filmDbStorage.save(film1);

        assertFalse(filmDbStorage.findAllFilms().isEmpty());
        assertEquals(1, filmDbStorage.findAllFilms().size());

    }

    @Test
    void testFindFilmById() {

        filmDbStorage.save(film1);

        Film fetchedFilm = filmDbStorage.findFilmById(film1.getId());
        assertNotNull(fetchedFilm);
        assertThrows(ObjectNotFoundException.class, () -> filmDbStorage.findFilmById(9999L));

    }

    @Test
    void testDeleteFilm() {

        filmDbStorage.save(film1);
        filmDbStorage.save(film2);

        Film fetchedFilm = filmDbStorage.findFilmById(2L);

        assertEquals(2, filmDbStorage.findAllFilms().size());
        filmDbStorage.delete(fetchedFilm);
        assertEquals(1, filmDbStorage.findAllFilms().size());
        assertThrows(ObjectNotFoundException.class, () -> filmDbStorage.delete(filmNotFind));

    }

    @Test
    void testIsNotExistsFilm() {

        assertTrue(filmDbStorage.isNotExistsFilm(9999L));

    }

}