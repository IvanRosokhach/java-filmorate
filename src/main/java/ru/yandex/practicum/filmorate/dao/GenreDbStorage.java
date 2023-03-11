package ru.yandex.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Primary
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre findGenre(int id) {
        if (exists(id)) {
            String sql = "SELECT * FROM genre WHERE genre_id = ?;";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), id);
        } else {
            throw new NotFoundException("Такого жанра нет");
        }
    }

    @Override
    public Collection<Genre> findAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    public boolean exists(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM genre WHERE genre_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    public Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

}
