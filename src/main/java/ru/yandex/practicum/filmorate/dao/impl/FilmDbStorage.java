//package ru.yandex.practicum.filmorate.dao.impl;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.support.rowset.SqlRowSet;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.Collection;
//
//@Slf4j
//@Component
////@Primary
//public class FilmDbStorage implements FilmStorage {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public FilmDbStorage(JdbcTemplate jdbcTemplate){
//        this.jdbcTemplate=jdbcTemplate;
//    }
//
//    @Override
//    public Film findFilm(long id){
//        // выполняем запрос к базе данных.
//        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
//
//        // обрабатываем результат выполнения запроса
//        if(filmRows.next()) {
//            Film film = new Film(
//                    filmRows.getLong("film_id"),
//                    filmRows.getString("titel"),
//                    filmRows.getString("description"),
//                    LocalDate.parse(filmRows.getString("releaseDate")),
//                    filmRows.getInt("duration"));
//
//            log.info("Найден фильм: {} {}", film.getId(), film.getName());
//
//            return film;
//        } else {
//            log.info("Фильм с идентификатором {} не найден.", id);
//            throw new NotFoundException("Фильм не найден.");
//        }
//    }
//
//    public Collection<Film> findAllFilms() {
//        String sql = "SELECT *\n" +
//                "FROM film\n" +
//                "WHERE film_id IN (SELECT film_id\n" +
//                "                  FROM likes\n" +
//                "                  GROUP BY film_id\n" +
//                "                  ORDER BY COUNT(user_id) DESC\n" +
//                "                  LIMIT 10);";
//        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
//    }
//
//    private Film makeFilm(ResultSet rs) throws SQLException {
//        return new Film(rs.getLong("film_id"),
//                rs.getString("titel"),
//                rs.getString("description"),
//                LocalDate.parse(rs.getString("releaseDate")),
//                rs.getInt("duration"));
//    }
//
//    public Film createFilm(Film film) {
//        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("INSERT INTO films (titel, description, releaseDate, duration) VALUES ( '?', '?', '?', '?');",
//                film.getName(),film.getDuration(),film.getReleaseDate().toString(),String.valueOf(film.getDuration()));
//        return film;
//    }
//
//    public Film updateFilm(Film film){
//        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("INSERT INTO films (titel, description, releaseDate, duration) VALUES ( '?', '?', '?', '?');",
//                film.getName(),film.getDuration(),film.getReleaseDate().toString(),String.valueOf(film.getDuration()));
//        return film;
//    }
//
//    public void deleteFilm(long id) {
//        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("DELETE FROM clients WHERE id = ?", id);
//    }
//}
