package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.IsAfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    final Set<Long> likes = new HashSet<>();

    @Positive
    Long id;
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