package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
        String sql = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM PUBLIC.USERS WHERE USER_ID=?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (userRows.next()) {
            User user = new User(
                    userRows.getLong("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        String sql = "SELECT * FROM users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        return findUser(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
    }

    @Override
    public User updateUser(User user) {
        long tempId = user.getId();
        if (!exists(tempId)) {
            throw new NotFoundException("Пользователь с идентификатором " + tempId + " не найден.");
        }
        String updateSql = "UPDATE PUBLIC.USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE USER_ID=?;";
        jdbcTemplate.update(updateSql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                tempId);
        return findUser(tempId);
    }

    @Override
    public void deleteUser(long id) {
        if (exists(id)) {
            jdbcTemplate.update("DELETE FROM PUBLIC.USERS WHERE USER_ID=?;", id);
        } else {
            log.info("Не найден пользователь с идентификатором: {}", id);
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
    }

    @Override
    public void addFriend(long id, long friendId) {
        if (!exists(id)) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
        if (!exists(friendId)) {
            throw new NotFoundException("Пользователь с идентификатором " + friendId + " не найден.");
        }
        String sql = "INSERT INTO PUBLIC.FRIENDS (USER_ID, OTHER_USER_ID, STATUS) VALUES(?, ?, ?);";
        jdbcTemplate.update(sql, id, friendId, "false");
    }

    public void deleteFriend(long id, long friendId) {
        if (!exists(id)) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
        if (!exists(friendId)) {
            throw new NotFoundException("Пользователь с идентификатором " + friendId + " не найден.");
        }
        String sql = "DELETE FROM PUBLIC.FRIENDS WHERE USER_ID=? AND OTHER_USER_ID=?;";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public Collection<User> getFriends(long id) {
        if (!exists(id)) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
        String sql = "SELECT * FROM PUBLIC.USERS WHERE USER_ID IN (SELECT OTHER_USER_ID FROM PUBLIC.FRIENDS WHERE USER_ID =?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public Collection<User> getCommonFriends(long id, long otherId) {
        if (!exists(id)) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
        if (!exists(otherId)) {
            throw new NotFoundException("Пользователь с идентификатором " + otherId + " не найден.");
        }
        String sql = "SELECT * FROM users " +
                "WHERE user_id IN (SELECT other_user_id FROM friends WHERE user_id=?) " +
                "AND user_id IN (SELECT other_user_id FROM friends WHERE user_id=?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, otherId);
    }


    public boolean exists(long id) {
        String sqlQuery = "select count(*) from users where user_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                LocalDate.parse(rs.getString("birthday")));
    }

    public User createUser2(User user) {
        String sql = "INSERT INTO PUBLIC.USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES(?, ?, ?, ?);";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()));
        return user;
    }

}
