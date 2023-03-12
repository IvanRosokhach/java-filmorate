package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    void userCRUD() {
        Collection<User> allUsers1 = userDbStorage.findAllUsers();
        assertEquals(0, allUsers1.size());

        User tempUser1 = User.builder()
                .email("test@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User user1 = userDbStorage.createUser(tempUser1);

        assertEquals(3, user1.getId());
        assertEquals(tempUser1.getEmail(), user1.getEmail());
        assertEquals(tempUser1.getLogin(), user1.getLogin());
        assertEquals(tempUser1.getName(), user1.getName());
        assertEquals(tempUser1.getBirthday(), user1.getBirthday());

        Collection<User> allUsers2 = userDbStorage.findAllUsers();
        assertEquals(1, allUsers2.size());

        User tempUserForUpdate = User.builder()
                .id(user1.getId())
                .email("user1@mail.ru")
                .login("User1")
                .name("User1")
                .birthday(LocalDate.of(1980, 12, 12))
                .build();

        user1 = userDbStorage.updateUser(tempUserForUpdate);
        assertEquals(3, user1.getId());
        assertEquals("user1@mail.ru", user1.getEmail());
        assertEquals("User1", user1.getLogin());
        assertEquals("User1", user1.getName());
        assertEquals(LocalDate.of(1980, 12, 12), user1.getBirthday());

        Collection<User> allUsers3 = userDbStorage.findAllUsers();
        assertEquals(1, allUsers3.size());

        User tempUser2 = User.builder()
                .email("user2@mail.ru")
                .login("User2")
                .name("User2")
                .birthday(LocalDate.of(2005, 10, 10))
                .build();
        User user2 = userDbStorage.createUser(tempUser2);

        User tempUser3 = User.builder()
                .email("user3@mail.ru")
                .login("User3")
                .name("User3")
                .birthday(LocalDate.of(2000, 5, 5))
                .build();
        User user3 = userDbStorage.createUser(tempUser3);

        Collection<User> allUsers4 = userDbStorage.findAllUsers();
        assertEquals(3, allUsers4.size());

        Collection<User> friendsUser1 = userDbStorage.getFriends(user1.getId());
        assertEquals(0, friendsUser1.size());

        userDbStorage.addFriend(user1.getId(), user2.getId());

        Collection<User> friendsUser2 = userDbStorage.getFriends(user1.getId());
        assertEquals(1, friendsUser2.size());

        Collection<User> friendsUser3 = userDbStorage.getFriends(user2.getId());
        assertEquals(0, friendsUser3.size());

        userDbStorage.addFriend(user1.getId(), user3.getId());
        userDbStorage.addFriend(user2.getId(), user3.getId());

        Collection<User> commonFriends = userDbStorage.getCommonFriends(user1.getId(), user2.getId());
        assertTrue(commonFriends.contains(user3));

        userDbStorage.deleteFriend(user1.getId(), user2.getId());
        Collection<User> friendsUser4 = userDbStorage.getFriends(user1.getId());
        assertEquals(1, friendsUser4.size());

        userDbStorage.deleteUser(user2.getId());
        Collection<User> allUsers5 = userDbStorage.findAllUsers();
        assertTrue(allUsers5.contains(user1));
        assertTrue(allUsers5.contains(user3));
        assertEquals(2, allUsers5.size());
    }

}
