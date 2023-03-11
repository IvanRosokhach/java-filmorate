package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface UserStorage {

    Map<Long, User> userData = new HashMap<>();
    Map<Long, Set<Long>> friends = new TreeMap<>();
    String notFoundUser = "Пользователь с id = %s не найден.";

    User findUser(long id);

    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    Collection<User> getFriends(long id);

    Collection<User> getCommonFriends(long id, long otherId);

}
