package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Map<Long, Film> filmData = new HashMap<>();

    Film findFilm(long id);

    Collection<Film> findAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(long id);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    List<Film> findPopularFilms(int count);

}
