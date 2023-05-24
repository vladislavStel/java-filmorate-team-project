package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private final FilmService filmService;

    Film film;
    String url = "/films";
    private final LocalDate testReleaseDate = LocalDate.of(2222, 1, 1);

    Film.FilmBuilder filmBuilder;

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    @BeforeEach
    void init() {

        int duration = 90;
        filmBuilder = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(testReleaseDate)
                .duration(duration)
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .mpa(new Mpa(1));

    }

    @Test
    void handleCreateMockMvc() {

        assertNotNull(mockMvc);

    }

    @Test
    void handleGetAllFilms_ReturnListFilms() throws Exception {

        when(filmService.getAllFilms()).thenReturn(List.of(filmBuilder.id(1L).build()));
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    void handleGetPopularFilms_ReturnListPopularFilms() throws Exception {

        Film film1 = filmBuilder.id(1L).name("Film name1").build();
        Film film2 = filmBuilder.id(2L).name("Film name2").build();
        when(filmService.getPopular(2L)).thenReturn(List.of(film1, film2));

        mockMvc.perform(get(url + "/popular?count=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$.size()", is(2)));

    }

    @Test
    void  handleGetFilmByID_ReturnStatus200AndCorrectJson() throws Exception {

        film = filmBuilder.id(1L).build();
        String json = objectMapper.writeValueAsString(film);

        when(filmService.getFilmByID(1L)).thenReturn(film);
        mockMvc.perform(get(url + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));

    }

    @Test
    void handleGetFilmByIdWhenNotExistingId_ReturnException() throws Exception {

        when(filmService.getFilmByID(999L)).thenThrow(new ObjectNotFoundException("Фильм с id 999 не найден"));
        mockMvc.perform(get(url + "/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
        assertThrows(ObjectNotFoundException.class, () -> filmService.getFilmByID(999L));

    }

    @Test
    void handleAddFilm_ReturnStatus200AndCorrectJson() throws Exception {

        film = filmBuilder.build();
        Film filmAdded = filmBuilder.id(1L).build();
        String json = objectMapper.writeValueAsString(film);
        String jsonAdded = objectMapper.writeValueAsString(filmAdded);

        when(filmService.addFilm(film)).thenReturn(filmAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonAdded));

    }

    @Test
    void handleUpdateFilm_ReturnStatus200AndCorrectJson() throws Exception {

        film = filmBuilder.id(1L).build();
        String json = objectMapper.writeValueAsString(film);

        when(filmService.updateFilm(film)).thenReturn(film);
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));

    }

    @Test
    void handleAddLikeByFilm_ReturnStatus200() throws Exception {

        mockMvc.perform(put(url + "/1/like/1"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void handleDeleteLikeByFilm_ReturnStatus200() throws Exception {

        mockMvc.perform(delete(url + "/1/like/1"))
                .andDo(print())
                .andExpect(status().isOk());

    }

}