package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private final GenreService genreService;

    String url = "/genres";
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void handleCreateMockMvc() {

        assertNotNull(mockMvc);

    }

    @Test
    void handleGetAllGenres_ReturnListFilms() throws Exception {

        when(genreService.getAllGenres()).thenReturn(List.of(new Genre(1, "Комедия")));

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    void handleGetGenreByID_ReturnStatus200AndCorrectJson() throws Exception {

        Genre genre = new Genre(1, "Комедия");
        String json = objectMapper.writeValueAsString(genre);

        when(genreService.getGenreById(1)).thenReturn(genre);
        mockMvc.perform(get(url + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));

    }

}