package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    private long idGenerator = 1;

    @Override
    public Film findFilm(long id) {
        if (filmData.containsKey(id)) {
            Film film = filmData.get(id);
            log.info("Найден фильм: {}.", film);
            return film;
        } else {
            log.info("Фильм с id = {} не найден.", id);
            throw new NotFoundException(String.format(notFoundFilm, id));
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        log.info("Текущее количество фильмов: {}.", filmData.size());
        return filmData.values();
    }

    @Override
    public Film createFilm(Film film) {
        List<Genre> tempGenre = new ArrayList<>();
        Set<Genre> unique = new HashSet<>(film.getGenres());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : unique) {
                tempGenre.add(genreStorage.findGenre(genre.getId()));
            }
        }
        Film tempFilm = Film.builder()
                .id(idGenerator++)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(mpaStorage.findMpa(film.getMpa().getId()))
                .genres(tempGenre)
                .build();
        filmData.put(tempFilm.getId(), tempFilm);
        likes.put(tempFilm.getId(), new TreeSet<>());
        log.info("Фильм добавлен: {}.", tempFilm);
        return tempFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmData.containsKey(film.getId())) {
            List<Genre> tempGenre = new ArrayList<>();
            Set<Genre> unique = new TreeSet<>(Comparator.comparingInt(Genre::getId));
            unique.addAll(film.getGenres());
            if (!film.getGenres().isEmpty()) {
                for (Genre genre : unique) {
                    tempGenre.add(genreStorage.findGenre(genre.getId()));
                }
            }
            Film tempFilm = Film.builder()
                    .id(film.getId())
                    .name(film.getName())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .duration(film.getDuration())
                    .mpa(mpaStorage.findMpa(film.getMpa().getId()))
                    .genres(tempGenre)
                    .build();
            filmData.put(tempFilm.getId(), tempFilm);
            log.info("Фильм обновлен: {}.", tempFilm);
            return tempFilm;
        } else {
            log.info("Не найден фильм: {}.", film);
            throw new NotFoundException(String.format(notFoundFilm, film.getId()));
        }
    }

    @Override
    public void deleteFilm(long id) {
        if (filmData.containsKey(id)) {
            filmData.remove(id);
            likes.remove(id);
            log.info("Фильм с id = {} удален.", id);
        } else {
            log.info("Фильм с id = {} не найден.", id);
            throw new NotFoundException(String.format(notFoundFilm, id));
        }
    }

    @Override
    public void addLike(long id, long userId) {
        Set<Long> users = likes.get(id);
        if (users == null) {
            throw new NotFoundException(String.format(notFoundFilm, id));
        }
        userStorage.findUser(userId);
        users.add(userId);
        log.info("Лайк добавлен.");
    }

    @Override
    public void deleteLike(long id, long userId) {
        Set<Long> users = likes.get(id);
        if (users == null) {
            throw new NotFoundException(String.format(notFoundFilm, id));
        }
        userStorage.findUser(userId);
        users.remove(userId);
        log.info("Лайк удален.");
    }

    @Override
    public Collection<Film> findPopularFilms(int count) {
        Collection<Film> films = findAllFilms().stream()
                .map(Film::getId)
                .sorted(Comparator.comparing(id -> likes.get(id).size()).reversed())
                .limit(count)
                .map(filmData::get)
                .collect(Collectors.toList());
        log.info("Текущее количество популярных фильмов: {}.", films.size());
        return films;
    }

}
