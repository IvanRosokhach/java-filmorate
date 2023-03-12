package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         UserDbStorage userDbStorage,
                         MpaDbStorage mpaDbStorage,
                         GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Film findFilm(long id) {
        String sql = "SELECT * FROM FILMS AS F " +
                "LEFT OUTER JOIN RATING AS R ON R.RATING_ID = F.RATING_ID " +
                "WHERE FILM_ID=?;";
        exists(id);
        Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        log.info("Найден фильм: {}.", film);
        return film;
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sql = "SELECT * FROM FILMS AS F " +
                "LEFT OUTER JOIN RATING AS R ON R.RATING_ID = F.RATING_ID " +
                "ORDER BY FILM_ID;";
        Collection<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        log.info("Текущее количество фильмов: {}.", films.size());
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long tempId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        if (film.getGenres() != null) {
            updateGenreForFilm(tempId, film.getGenres());
        }
        Film tempFilm = findFilm(tempId);
        log.info("Фильм добавлен: {}.", tempFilm);
        return tempFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        long tempId = film.getId();
        exists(tempId);
        String updateSql = "UPDATE FILMS SET TITEL=?, DESCRIPTION=?, RELEASEDATE=?, DURATION=?, RATING_ID=? " +
                "WHERE FILM_ID=?;";
        jdbcTemplate.update(updateSql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                tempId);
        updateGenreForFilm(film.getId(), film.getGenres());
        Film tempFilm = findFilm(tempId);
        log.info("Фильм обновлен: {}.", tempFilm);
        return tempFilm;
    }

    private void updateGenreForFilm(long filmId, List<Genre> genres) {
        String sqlDelete = "DELETE FROM FILM_GENRE WHERE FILM_ID=?;";
        jdbcTemplate.update(sqlDelete, filmId);
        if (genres != null) {
            String sqlInsert = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?, ?);";
            for (Genre genre : genres) {
                jdbcTemplate.update(sqlInsert, filmId, genre.getId());
            }
        }
    }

    @Override
    public void deleteFilm(long id) {
        exists(id);
        jdbcTemplate.update("DELETE FROM FILMS WHERE FILM_ID=?", id);
        log.info("Фильм с id = {} удален.", id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        exists(filmId);
        userDbStorage.exists(userId);
        String sql = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES(?, ?);";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Лайк добавлен.");
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        exists(filmId);
        userDbStorage.exists(userId);
        String sql = "DELETE FROM LIKES WHERE FILM_ID=? AND USER_ID=?;";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Лайк удален.");
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        String sql = "SELECT F.*, R.*, COUNT(L.FILM_ID) AS all_likes " +
                "FROM FILMS AS F " +
                "LEFT JOIN RATING AS R ON R.RATING_ID = F.RATING_ID " +
                "LEFT JOIN LIKES AS L ON F.FILM_ID=L.FILM_ID " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY all_likes DESC " +
                "LIMIT ?;";
        Collection<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
        log.info("Текущее количество популярных фильмов: {}.", films.size());
        return films;
    }

    public boolean exists(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID=?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        if (result != 1) {
            throw new NotFoundException(String.format(notFoundFilm, id));
        }
        return result == 1;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long tempId = rs.getLong("film_id");
        String sqlGenre = "SELECT * FROM GENRE WHERE GENRE_ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID=?);";
        List<Genre> genres = new ArrayList<>();
        genres = jdbcTemplate.query(sqlGenre, (result, rowNum) -> genreDbStorage.makeGenre(result), tempId);
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("titel"))
                .description(rs.getString("description"))
                .releaseDate(LocalDate.parse(rs.getString("releaseDate")))
                .duration(rs.getInt("duration"))
                .mpa(mpaDbStorage.makeMpa(rs))
                .genres(genres)
                .build();
    }

}
