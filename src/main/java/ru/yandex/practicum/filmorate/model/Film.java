package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {

    private int id;
    @NotNull
    @NotBlank(message = "Название не может быть пустым.")
    private final String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private final int duration;
}
