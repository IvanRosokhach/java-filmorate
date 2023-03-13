package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void filmCRUD() {
        Collection<Film> allFilms1 = filmDbStorage.findAllFilms();
        assertEquals(0, allFilms1.size());

        Film tempFilm1 = Film.builder()
                .name("testFilm")
                .description("testFilm")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(Mpa.builder().id(1).build())
                .build();

        Film film1 = filmDbStorage.createFilm(tempFilm1);

        assertEquals(1, film1.getId());
        assertEquals(tempFilm1.getName(), film1.getName());
        assertEquals(tempFilm1.getDescription(), film1.getDescription());
        assertEquals(tempFilm1.getReleaseDate(), film1.getReleaseDate());
        assertEquals(tempFilm1.getDuration(), film1.getDuration());
        assertEquals(1, film1.getMpa().getId());
        assertTrue(film1.getGenres().isEmpty());

        Collection<Film> allFilms2 = filmDbStorage.findAllFilms();
        assertEquals(1, allFilms2.size());

        Film tempFilmForUpdate = Film.builder()
                .id(film1.getId())
                .name("Film1")
                .description("Descr1")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(120)
                .mpa(Mpa.builder().id(2).build())
                .genres(List.of(Genre.builder().id(2).build()))
                .build();

        film1 = filmDbStorage.updateFilm(tempFilmForUpdate);

        assertEquals(1, film1.getId());
        assertEquals("Film1", film1.getName());
        assertEquals("Descr1", film1.getDescription());
        assertEquals(LocalDate.of(2010, 10, 10), film1.getReleaseDate());
        assertEquals(120, film1.getDuration());
        assertEquals(2, film1.getMpa().getId());
        assertEquals("PG", film1.getMpa().getName());
        assertEquals(2, film1.getGenres().get(0).getId());

        Collection<Film> allFilms3 = filmDbStorage.findAllFilms();
        assertEquals(1, allFilms3.size());

        Film tempFilm2 = Film.builder()
                .name("Film2")
                .description("Descr2")
                .releaseDate(LocalDate.of(2012, 12, 12))
                .duration(90)
                .mpa(Mpa.builder().id(3).build())
                .genres(List.of(Genre.builder().id(1).build()))
                .build();
        Film film2 = filmDbStorage.createFilm(tempFilm2);

        tempFilmForUpdate = Film.builder()
                .id(film2.getId())
                .name("Film2")
                .description("Descr2")
                .releaseDate(LocalDate.of(2020, 10, 21))
                .duration(120)
                .mpa(Mpa.builder().id(2).build())
                .build();

        film2 = filmDbStorage.updateFilm(tempFilmForUpdate);
        assertTrue(film2.getGenres().isEmpty());

        Collection<Film> allFilms4 = filmDbStorage.findAllFilms();
        assertEquals(2, allFilms4.size());

        User user1 = userDbStorage.createUser(User.builder()
                .email("user1@mail.ru")
                .login("User1")
                .name("User1")
                .birthday(LocalDate.of(1980, 12, 12))
                .build());

        User user2 = userDbStorage.createUser(User.builder()
                .email("user2@mail.ru")
                .login("User2")
                .name("User2")
                .birthday(LocalDate.of(2005, 8, 15))
                .build());

        List<Film> popularFilms1 = (List<Film>) filmDbStorage.findPopularFilms(10);
        assertEquals(1, popularFilms1.get(0).getId());

        filmDbStorage.addLike(film2.getId(), user1.getId());
        List<Film> popularFilms2 = (List<Film>) filmDbStorage.findPopularFilms(10);
        assertEquals(2, popularFilms2.get(0).getId());

        filmDbStorage.addLike(film1.getId(), user2.getId());
        List<Film> popularFilms3 = (List<Film>) filmDbStorage.findPopularFilms(10);
        assertEquals(1, popularFilms3.get(0).getId());

        filmDbStorage.deleteLike(film1.getId(), user2.getId());
        List<Film> popularFilms4 = (List<Film>) filmDbStorage.findPopularFilms(10);
        assertEquals(2, popularFilms4.get(0).getId());

        filmDbStorage.deleteFilm(film2.getId());

        Collection<Film> allFilms5 = filmDbStorage.findAllFilms();
        assertEquals(1, allFilms5.size());

        List<Film> popularFilms5 = (List<Film>) filmDbStorage.findPopularFilms(10);
        assertEquals(1, popularFilms5.get(0).getId());
        userDbStorage.deleteUser(user1.getId());
        userDbStorage.deleteUser(user2.getId());
    }

}
