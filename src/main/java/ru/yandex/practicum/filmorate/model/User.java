package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Builder
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
    @JsonIgnore
    private final Set<Long> friends = new HashSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        return values;
    }

}
