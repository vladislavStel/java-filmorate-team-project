package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}