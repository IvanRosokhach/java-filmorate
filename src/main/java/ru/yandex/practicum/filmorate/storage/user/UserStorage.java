package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface UserStorage {

    Map<Long, User> userData = new HashMap<>();

    User findUser(long id);

    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long id);
}
