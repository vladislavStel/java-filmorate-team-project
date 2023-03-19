package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotBlank;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    Long id;
    @NotBlank
    String name;
    @NotBlank
    @Size(max = 200)
    String description;
    @NotNull
    LocalDate releaseDate;
    @Positive
    int duration;
}