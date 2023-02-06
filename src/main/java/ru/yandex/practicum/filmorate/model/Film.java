package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {

    private long id;
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
