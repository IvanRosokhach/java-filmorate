package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private long idGenerator = 1;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Текущее количество пользователей: {}.", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (users.containsValue(user)) {
            log.info("Такой пользователь уже зарегистрирован: {}.", user);
            throw new ValidationException("Такой пользователь уже зарегистрирован.");
        }
        validate(user);
        user.setId(idGenerator++);
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}.", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь обновлен: {}.", user);
        } else {
            log.info("Не найден пользователь: {}.", user);
            throw new ValidationException("Пользователь не найден.");
        }
        return user;
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.info("Логин не может содержать пробелы: {}.", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Автоматически добавлено имя пользователя");
        }
    }
}
