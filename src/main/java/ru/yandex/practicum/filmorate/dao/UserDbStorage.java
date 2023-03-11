package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User findUser(long id) {
        String sql = "SELECT * FROM USERS WHERE USER_ID=?;";
        if (exists(id)) {
            User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
            log.info("Найден пользователь: {}.", user);
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException(String.format(notFoundUser, id));
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        String sql = "SELECT * FROM USERS;";
        Collection<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        log.info("Текущее количество пользователей: {}.", users.size());
        return users;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        User tempUser = findUser(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        log.info("Пользователь добавлен: {}.", tempUser);
        return tempUser;
    }

    @Override
    public User updateUser(User user) {
        long tempId = user.getId();
        if (exists(tempId)) {
            String updateSql = "UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE USER_ID=?;";
            jdbcTemplate.update(updateSql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    tempId);
            User tempUser = findUser(tempId);
            log.info("Пользователь обновлен: {}.", tempUser);
            return tempUser;
        } else {
            throw new NotFoundException(String.format(notFoundUser, tempId));
        }
    }

    @Override
    public void deleteUser(long id) {
        if (exists(id)) {
            jdbcTemplate.update("DELETE FROM USERS WHERE USER_ID=?;", id);
            log.info("Пользователь с id = {} удалён.", id);
        } else {
            log.info("Не найден пользователь с идентификатором: {}", id);
            throw new NotFoundException(String.format(notFoundUser, id));
        }
    }

    @Override
    public void addFriend(long id, long friendId) {
        if (!exists(id)) {
            throw new NotFoundException(String.format(notFoundUser, id));
        }
        if (!exists(friendId)) {
            throw new NotFoundException(String.format(notFoundUser, friendId));
        }
        String sql = "INSERT INTO FRIENDS (USER_ID, OTHER_USER_ID) VALUES(?, ?);";
        jdbcTemplate.update(sql, id, friendId);
        log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}.", id, friendId);
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        if (!exists(id)) {
            throw new NotFoundException(String.format(notFoundUser, id));
        }
        if (!exists(friendId)) {
            throw new NotFoundException(String.format(notFoundUser, friendId));
        }
        String sql = "DELETE FROM FRIENDS WHERE USER_ID=? AND OTHER_USER_ID=?;";
        jdbcTemplate.update(sql, id, friendId);
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}.", id, friendId);
    }

    @Override
    public Collection<User> getFriends(long id) {
        if (!exists(id)) {
            throw new NotFoundException(String.format(notFoundUser, id));
        }
        String sql = "SELECT * FROM USERS WHERE USER_ID IN " +
                "(SELECT OTHER_USER_ID FROM FRIENDS WHERE USER_ID=?)";
        Collection<User> userFriends = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        log.info("Количество друзей у пользователя с id {}: {}.", id, userFriends.size());
        return userFriends;
    }

    @Override
    public Collection<User> getCommonFriends(long id, long otherId) {
        if (!exists(id)) {
            throw new NotFoundException(String.format(notFoundUser, id));
        }
        if (!exists(otherId)) {
            throw new NotFoundException(String.format(notFoundUser, otherId));
        }
        String sql = "SELECT * FROM USERS WHERE USER_ID IN " +
                "(SELECT OTHER_USER_ID FROM FRIENDS WHERE USER_ID=?) " +
                "AND USER_ID IN (SELECT OTHER_USER_ID FROM FRIENDS WHERE USER_ID=?)";
        Collection<User> commonFriends = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, otherId);
        log.info("Количество общих друзей: {}, у пользователей с id: {} и {}.", commonFriends.size(), id, otherId);
        return commonFriends;
    }


    boolean exists(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM USERS WHERE USER_ID=?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(LocalDate.parse(rs.getString("birthday")))
                .build();
    }

}
