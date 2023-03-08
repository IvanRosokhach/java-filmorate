//package ru.yandex.practicum.filmorate.storage.user;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
////@Primary
//public class InMemoryUserStorage implements UserStorage {
//
//    private long idGenerator = 1;
//
//    @Override
//    public User findUser(long id) {
//        if (userData.containsKey(id)) {
//            return userData.get(id);
//        } else {
//            log.info("Пользователь с id = {} не найден.", id);
//            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
//        }
//    }
//
//    @Override
//    public Collection<User> findAllUsers() {
//        log.info("Текущее количество пользователей: {}.", userData.size());
//        return userData.values();
//    }
//
//    @Override
//    public User createUser(User user) {
//        user.setId(idGenerator++);
//        userData.put(user.getId(), user);
//        log.info("Пользователь добавлен: {}.", user);
//        return user;
//    }
//
//    @Override
//    public User updateUser(User user) {
//        if (userData.containsKey(user.getId())) {
//            userData.put(user.getId(), user);
//            log.info("Пользователь обновлен: {}.", user);
//        } else {
//            log.info("Не найден пользователь: {}.", user);
//            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден.");
//        }
//        return user;
//    }
//
//    @Override
//    public void deleteUser(long id) {
//        if (userData.containsKey(id)) {
//            for (Long friendsId : findUser(id).getFriends()) {
//                findUser(friendsId).getFriends().removeIf(tempId -> tempId == id);
//            }
//            userData.remove(id);
//            log.info("Пользователь с id = {} удалён.", id);
//        } else {
//            log.info("Пользователь с id = {} не найден.", id);
//            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
//        }
//    }
//
//    @Override
//    public void addFriend(long id, long friendId) {
//        findUser(id).getFriends().add(friendId);
//        findUser(friendId);
//        log.info("Пользователи с id: {} и {} стали друзьями.", id, friendId);
//    }
//
//    @Override
//    public void deleteFriend(long id, long friendId) {
//        findUser(id).getFriends().remove(findUser(friendId).getId());
//        findUser(friendId);
//        log.info("Пользователь удален из списка друзей.");
//    }
//
//    @Override
//    public Collection<User> getFriends(long id) {
//        List<User> userFriends = new ArrayList<>();
//        findAllUsers().forEach(user -> findUser(id).getFriends().stream()
//                .filter(idList -> user.getId() == idList)
//                .map(idList -> user)
//                .forEach(userFriends::add));
//        log.info("Количество друзей у пользователя {}: {}.", findUser(id).getName(), userFriends.size());
//        return userFriends;
//    }
//
//    @Override
//    public Collection<User> getCommonFriends(long id, long otherId) {
//        List<User> commonFriends = getFriends(id).stream()
//                .filter(getFriends(otherId)::contains)
//                .collect(Collectors.toList());
//        log.info("Количество общих друзей: {}.", commonFriends.size());
//        return commonFriends;
//    }
//
//}
