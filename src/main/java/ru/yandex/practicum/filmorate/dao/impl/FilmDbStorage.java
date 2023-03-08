package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
        String sql = "SELECT * FROM FILMS left outer join rating AS r on r.rating_id = Films.rating_id WHERE film_ID = ?;";
        if (exists(id)) {
            Film film;
            try {
                log.info("findFilm UP");
                film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), new Object[]{id});
                log.info("findFilm DONE " + film);
            } catch (EmptyResultDataAccessException ex) {
                log.info("findFilm CATCH");
                throw new NotFoundException("catch find film " + id);
            }
//            log.info("findFilm CATCH select genre ");
//            String sqlGenre = "SELECT * FROM genre WHERE genre_id IN (SELECT genre_id FROM film_genre WHERE film_id = ?);";
//            List<Genre> genres = jdbcTemplate.query(sqlGenre, (rs, rowNum) -> genreDbStorage.makeGenre(rs), id);
//            log.info("findFilm CATCH select genre DONE");
//            film.setGenres(genres);
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sql = "SELECT * FROM FILMS left outer join rating AS r on r.rating_id = Films.rating_id ORDER BY film_ID;";
        //"SELECT * FROM films"; //WHERE film_id IN (SELECT film_id FROM likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT 10);";
        log.info("findAllFilms UP -> make film");
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        String sql = "SELECT films.*, r.*, COUNT(likes.film_id) as all_likes\n" +
                "    FROM films\n" +
                "    left join rating AS r on r.rating_id = Films.rating_id" +
                "    LEFT JOIN likes ON films.film_id=likes.film_id\n" +
                "    GROUP BY films.film_id\n" +
                "    ORDER BY all_likes DESC\n" +
                "    LIMIT ?;";
        log.info("findPOP");
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs),count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
//        log.info("makeFilm UP " + rs );
        long tempId = rs.getLong("film_id");
//        log.info("makeFilm UP " + tempId );
//        String sqlGenre = "SELECT * FROM public.genre WHERE genre_id IN (SELECT genre_id FROM public.film_genre WHERE film_id = ?);"; //ok
//        List<Genre> genres = new ArrayList<>();
//        try{
//            log.info("makeFilm TRY find Genres");
//        genres = jdbcTemplate.query(sqlGenre, (resultSet, rowNum) -> genreDbStorage.makeGenre(rs), tempId);
//            log.info("makeFilm DONE find Genres");
//        } catch (EmptyResultDataAccessException ex) {
//            log.info("makeFilm find Genres exception");
//            genres = new ArrayList<>();
//            throw new NotFoundException("catch make film ");
//        }
        log.info("findFilm CATCH select genre ");
        String sqlGenre = "SELECT * FROM genre WHERE genre_id IN (SELECT genre_id FROM film_genre WHERE film_id = ?);";
        List<Genre> genres = new ArrayList<>();
        try {
            genres = jdbcTemplate.query(sqlGenre, (result, rowNum) -> genreDbStorage.makeGenre(result), tempId);
        } catch (Exception e) {
            throw new NotFoundException("genre not");
        }
        log.info("findFilm CATCH select genre DONE");
//        film.setGenres(genres);
        log.info("Film.builder UP");
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("titel"))
                .description(rs.getString("description"))
                .releaseDate(LocalDate.parse(rs.getString("releaseDate")))
                .duration(rs.getInt("duration"))
                .mpa(mpaDbStorage.makeMpa(rs)).build();
        film.setGenres(genres);
        return film;
    }


    @Override
    public Film createFilm(Film film) {
        log.info("CreateFilm INSERT " + film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long tempId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        log.info("CreateFilm  " + film + "\n Genre field INSERT");
        if (film.getGenres() != null) {
            if (!film.getGenres().isEmpty()) {
                updateGenreForFilm(tempId, film.getGenres());
                log.info("CreateFilm " + film + "\n Genre field INSERT DONE");
            }
        }
        log.info("CreateFilm DOWN " + film);
        return findFilm(tempId);
    }

    public void updateGenreForFilm(long filmId, List<Genre> genres) {
        String sqlDelete = "delete from PUBLIC.FILM_GENRE where film_id = ?;";
        jdbcTemplate.update(sqlDelete, filmId);
        String sqlInsert = "INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?, ?);";  //ok
        for (Genre genre : genres) {
            log.info("updateGenreForFilm " + genre);
            jdbcTemplate.update(sqlInsert, filmId, genre.getId());
        }
    }

    @Override
    public Film updateFilm(Film film) {
        long tempId = film.getId();
        if (exists(tempId)) {
            String updateSql = "UPDATE PUBLIC.FILMS SET TITEL=?, DESCRIPTION=?, RELEASEDATE=?, DURATION=?, RATING_ID=? WHERE FILM_ID = ?;";
            jdbcTemplate.update(updateSql,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId(),
                    tempId);
            updateGenreForFilm(film.getId(), film.getGenres());
            return findFilm(tempId);
        } else {
            throw new NotFoundException("NOT F");
        }
    }

    @Override
    public void deleteFilm(long id) {
        if (exists(id)) {
            jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        } else {
        throw new NotFoundException("NOT F D");
    }
    }

    @Override
    public void addLike(long filmId, long userId) {
        if (exists(filmId) && (userDbStorage.exists(userId))) {
            String sql = "INSERT INTO PUBLIC.LIKES (FILM_ID, USER_ID) VALUES(?, ?);";
            jdbcTemplate.update(sql, filmId, userId);
        } else {
            throw new NotFoundException("");
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        if (exists(filmId) && (userDbStorage.exists(userId))) {
            String sql = "DELETE FROM PUBLIC.LIKES WHERE film_id = ? AND user_id = ?;";
            jdbcTemplate.update(sql, filmId, userId);
        } else {
            throw new NotFoundException("");
        }
    }

    public boolean exists(long id) {
        String sqlQuery = "select count(*) from films where film_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }
}
//}
//                return new Film(rs.getLong("film_id"),
//                rs.getString("titel"),
//                rs.getString("description"),
//                LocalDate.parse(rs.getString("releaseDate")),
//                rs.getInt("duration"),
//                mpaDbStorage.makeMpa(rs));

//genres);  //.getInt("rating_id")));
//}

//    private Film makeFilm2(ResultSet rs) throws SQLException {
//        Film film = new Film();
//        film.setId(rs.getLong("film_id"));
//        film.setName(rs.getString("titel"));
//        film.setDescription(rs.getString("description"));
//        film.setReleaseDate(LocalDate.parse(rs.getString("releaseDate")));
//        film.setDuration(rs.getInt("duration"));
//        film.setMpa(mpaDbStorage.makeMpa(rs));//.getInt("rating_id")));
//        return film;
//    }

//    @Override
//    public Film createFilm(Film film) {
//        jdbcTemplate.update("INSERT INTO films (titel, description, releaseDate, duration, rating_id) VALUES ( ?, ?, ?, ?, ?);",
//                film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId());
//        return film;
//    }


//@Override
//    public Film findFilm(long id) {
//        String sql = "SELECT FILM_ID, TITEL, DESCRIPTION, RELEASEDATE, DURATION,  FROM FILMS WHERE film_ID = ?;";
//        if (exists(id)) {
//            Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
//            return film;
//        } else {
//            log.info("Фильм с идентификатором {} не найден.", id);
//            throw new NotFoundException("Фильм не найден.");
//        }
//    }


//            // выполняем запрос к базе данных.
//        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
//
//        // обрабатываем результат выполнения запроса
//        if(filmRows.next()) {
//            Film film = new Film(
//                    filmRows.getLong("film_id"),
//                    filmRows.getString("titel"),
//                    filmRows.getString("description"),
//                    LocalDate.parse(filmRows.getString("releaseDate")),
//                    filmRows.getInt("duration"),
//                    filmRows.getMpa.getId().getValue);
//
//            log.info("Найден фильм: {} {}", film.getId(), film.getName());
//
//            return film;
//        } else {
//            log.info("Фильм с идентификатором {} не найден.", id);
//            throw new NotFoundException("Фильм не найден.");
//        }
//}
