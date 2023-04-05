package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.IsAfterDate;

import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    final Set<Long> likes = new HashSet<>();

    Long ID;
    @NotBlank
    String name;
    @NotBlank
    @Size(max = 200)
    String description;
    @NotNull
    @IsAfterDate
    LocalDate releaseDate;
    @Positive
    int duration;
}