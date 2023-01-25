package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private int id;
    @NotNull
    @Email(message = "Некорректная электронная почта.")
    private final String email;
    @NotNull
    @NotBlank(message = "Логин не может быть пустым.")
    private final String login;
    private String name;
    @NotNull
    @Past(message = "Дата рождения не может быть в будущем.")
    private final LocalDate birthday;
}
