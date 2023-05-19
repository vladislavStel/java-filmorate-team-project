package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    private static User user1;
    private static User user2;
    private static User userNotFind;

    @BeforeEach
    void beforeEach() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, "USERS");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1");

    }

    @BeforeAll
    public static void init() {

        user1 = User.builder()
                .login("User1")
                .birthday(LocalDate.of(1980, 10, 20))
                .email("user1@ya.ru")
                .build();
        user1.setName("Пользователь 1");

        user2 = User.builder()
                .login("User2")
                .birthday(LocalDate.of(1990, 5, 10))
                .email("user2@ya.ru")
                .build();
        user2.setName("Пользователь 2");

        userNotFind = User.builder()
                .login("User")
                .birthday(LocalDate.of(2000, 1, 1))
                .email("user@mail.ru")
                .build();
        user1.setName("Пользователь 1");
        userNotFind.setId(9999L);

    }

    @Test
    void testSaveUser() {

        userDbStorage.save(user1);

        User fetchedUser = userDbStorage.findUserById(user1.getId());

        assertEquals(1, fetchedUser.getId());
        assertEquals(user1.getLogin(), fetchedUser.getLogin());
        assertEquals(user1.getName(), fetchedUser.getName());

    }

    @Test
    void testUpdateUser() {

        User user1updated = User.builder()
                .login("User1update")
                .birthday(LocalDate.of(1980, 10, 20))
                .email("user1update@ya.ru")
                .build();
        user1updated.setId(1L);
        user1.setName("Пользователь 1update");

        userDbStorage.save(user1);
        userDbStorage.update(user1updated);

        User fetchedUser = userDbStorage.findUserById(user1updated.getId());

        assertEquals(user1.getId(), user1updated.getId());
        assertEquals(user1updated.getName(), fetchedUser.getName());
        assertEquals(user1updated.getLogin(), fetchedUser.getLogin());
        assertEquals(user1updated.getEmail(), fetchedUser.getEmail());

    }

    @Test
    void testFindAllUsers() {

        userDbStorage.save(user1);

        assertFalse(userDbStorage.findAllUsers().isEmpty());
        assertEquals(1, userDbStorage.findAllUsers().size());


    }

    @Test
    void testFindUserById() {

        userDbStorage.save(user1);

        User fetchedUser = userDbStorage.findUserById(user1.getId());
        assertNotNull(fetchedUser);

    }

    @Test
    void testDeleteUser() {

        userDbStorage.save(user1);
        userDbStorage.save(user2);

        User fetchedUser = userDbStorage.findUserById(2L);

        assertEquals(2, userDbStorage.findAllUsers().size());
        userDbStorage.delete(fetchedUser);
        assertEquals(1, userDbStorage.findAllUsers().size());
        assertThrows(ObjectNotFoundException.class, () -> userDbStorage.delete(userNotFind));


    }

    @Test
    void testIsNotExistsUser() {

        assertTrue(userDbStorage.isNotExistsUser(9999L));

    }
}