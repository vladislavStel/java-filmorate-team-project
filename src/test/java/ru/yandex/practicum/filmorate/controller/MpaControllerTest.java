package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MpaController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private final MpaService mpaService;

    String url = "/mpa";
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void handleCreateMockMvc() {

        assertNotNull(mockMvc);

    }

    @Test
    void handleGetAllMpa_ReturnListFilms() throws Exception {

        when(mpaService.getAllMpa()).thenReturn(List.of(new Mpa(1, "PG-13")));

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    void getMpa() throws Exception {

        Mpa mpa = new Mpa(1, "PG-13");
        String json = objectMapper.writeValueAsString(mpa);

        when(mpaService.getMpa(1)).thenReturn(mpa);
        mockMvc.perform(get(url + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));

    }

}