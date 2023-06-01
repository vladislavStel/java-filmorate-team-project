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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private final UserService userService;

    User user;
    String url = "/users";

    User.UserBuilder userBuilder;

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    @BeforeEach
    void init() {

        userBuilder = User.builder()
                .email("email@mail.ru")
                .login("login")
                .birthday(LocalDate.of(2000, 10, 6));

    }

    @Test
    void handleCreateMockMvc() {

        assertNotNull(mockMvc);

    }

    @Test
    void handleGetAllUsers_ReturnListUsers() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of(userBuilder.id(1L).build()));
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$.size()", is(1)));

    }

    @Test
    void handleGetUserByID_ReturnStatus200AndCorrectJson() throws Exception {

        user = userBuilder.id(1L).build();
        String json = objectMapper.writeValueAsString(user);

        when(userService.getUserById(1L)).thenReturn(user);
        mockMvc.perform(get(url + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));

    }

    @Test
    void handleGetUserByIdWhenNotExistingId_ReturnException() throws Exception {

        when(userService.getUserById(999L)).thenThrow(new ObjectNotFoundException("Пользователь с id 999 не найден"));
        mockMvc.perform(get(url + "/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
        assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(999L));

    }

    @Test
    void handleAddUser_ReturnStatus200AndCorrectJson() throws Exception {

        user = userBuilder.build();
        User userAdded = userBuilder.id(1L).build();
        String json = objectMapper.writeValueAsString(user);
        String jsonAdded = objectMapper.writeValueAsString(userAdded);

        when(userService.addUser(user)).thenReturn(userAdded);
        this.mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonAdded));

    }

    @Test
    void handleUpdateUser_ReturnStatus200AndCorrectJson() throws Exception {

        user = userBuilder.id(1L).build();
        String json = objectMapper.writeValueAsString(user);

        when(userService.updateUser(user)).thenReturn(user);
        this.mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));

    }

    @Test
    void handleAddFriendByUser_ReturnStatus200() throws Exception {

        mockMvc.perform(put(url + "/1/friends/2"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void handleDeleteFriendByUser_ReturnStatus200() throws Exception {

        mockMvc.perform(delete(url + "/1/friends/2"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void handleGetFriendsUser_ReturnListFriends() throws Exception {

        when(userService.getListFriends(1L)).thenReturn(List.of(
                userBuilder.id(2L).login("login2").email("email2@email.ru").name("name2").build()));
        mockMvc.perform(get(url + "/1/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(2)));

    }

    @Test
    void handleGetCommonFriendsUser_ReturnListCommonFriends() throws Exception {

        when(userService.getListOfCommonFriends(1L, 2L)).thenReturn(List.of(
                userBuilder.id(3L).login("login3").email("email3@email.ru").name("name3").build()));
        mockMvc.perform(get(url + "/1/friends/common/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(3)));
    }

}
