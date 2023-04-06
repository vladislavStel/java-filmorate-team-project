package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Positive
    Long id;

    @NotBlank
    @Pattern(regexp = "[A-Za-z]+(?:(?:, |-)[A-Za-z]+)*")
    String login;

    String name;

    @PastOrPresent
    LocalDate birthday;

    @NotBlank
    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "Не соответствует формату email адреса")
    String email;

    final Set<Long> friends = new HashSet<>();
}