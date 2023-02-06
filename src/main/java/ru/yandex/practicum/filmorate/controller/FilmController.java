package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private long idGenerator = 1;
    private static final LocalDate startCinema = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAllFilms() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (films.containsValue(film)) {
            log.info("Такой фильм уже создан: {}.", film);
            throw new ValidationException("Такой фильм уже создан.");
        }
        validate(film);
        film.setId(idGenerator++);
        films.put(film.getId(), film);
        log.info("Фильм добавлен: {}.", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм обновлен: {}.", film);
        } else {
            log.info("Не найден фильм: {}.", film);
            throw new ValidationException("Фильм не найден.");
        }
        return film;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(startCinema)) {
            log.info("Дата релиза: {} — должна быть не раньше 28 декабря 1895 года.", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
    }
}
