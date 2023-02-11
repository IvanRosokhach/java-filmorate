package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private static final LocalDate startCinema = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film findFilm(int id) {
        return filmStorage.findFilm(id);
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        validate(film);
        filmStorage.createFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        validate(film);
        filmStorage.updateFilm(film);
        return film;
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    public void addLike(int id, int userId) {
        filmStorage.findFilm(id).getLikes().add(userService.findUser(userId).getId());
        log.info("Лайк добавлен.");
    }

    public void deleteLike(int id, int userId) {
        filmStorage.findFilm(id).getLikes().remove(userService.findUser(userId).getId());
        log.info("Лайк удален.");
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(startCinema)) {
            log.info("Дата релиза: {} — должна быть не раньше 28 декабря 1895 года.", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
    }

    private int compare(Film film0, Film film1) {
        return -1 * (film0.getLikes().size() - film1.getLikes().size());
    }

}
