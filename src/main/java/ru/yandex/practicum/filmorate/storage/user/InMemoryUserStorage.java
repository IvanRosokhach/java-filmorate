package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private long idGenerator = 1;

    @Override
    public User findUser(long id) {
        if (!userData.containsKey(id)) {
            log.info("Пользователь с id = {} не найден.", id);
            throw new NotFoundException("Пользователь не найден.");
        }
        return userData.get(id);
    }

    @Override
    public Collection<User> findAllUsers() {
        log.info("Текущее количество пользователей: {}.", userData.size());
        return userData.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(idGenerator++);
        userData.put(user.getId(), user);
        log.info("Пользователь добавлен: {}.", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (userData.containsKey(user.getId())) {
            userData.put(user.getId(), user);
            log.info("Пользователь обновлен: {}.", user);
        } else {
            log.info("Не найден пользователь: {}.", user);
            throw new NotFoundException("Пользователь не найден.");
        }
        return user;
    }

    @Override
    public void deleteUser(long id) {
        if (userData.containsKey(id)) {
            userData.remove(id);
            log.info("Пользователь с id = {} удалён.", id);
        } else {
            log.info("Пользователь с id = {} не найден.", id);
            throw new NotFoundException("Пользователь не найден.");
        }
    }

}
