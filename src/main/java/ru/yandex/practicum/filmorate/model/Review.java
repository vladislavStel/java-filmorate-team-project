package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    @Positive
    Long reviewId;

    @NotBlank
    String content;

    @NotNull
    Boolean isPositive;

    @NotNull
    Long userId;

    @NotNull
    Long filmId;

    @NotNull
    int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful);
        return values;
    }

}