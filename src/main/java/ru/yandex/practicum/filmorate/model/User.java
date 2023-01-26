package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {

    private long id;
    @NotNull
    @Email(message = "Некорректная электронная почта.")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым.")
    private final String login;
    private String name;
    @NotNull
    @Past(message = "Дата рождения не может быть в будущем.")
    private final LocalDate birthday;
}
