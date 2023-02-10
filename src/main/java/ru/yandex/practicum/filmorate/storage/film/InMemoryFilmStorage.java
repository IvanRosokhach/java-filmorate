package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private long idGenerator = 1;

    @Override
    public Film findFilm(long id) {
        if (!filmData.containsKey(id)) {
            log.info("Фильм с id = {} не найден.", id);
            throw new NotFoundException("Фильм не найден.");
        }
        return filmData.get(id);
    }

    @Override
    public Collection<Film> findAllFilms() {
        log.info("Текущее количество фильмов: {}.", filmData.size());
        return filmData.values();
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(idGenerator++);
        filmData.put(film.getId(), film);
        log.info("Фильм добавлен: {}.", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmData.containsKey(film.getId())) {
            filmData.put(film.getId(), film);
            log.info("Фильм обновлен: {}.", film);
        } else {
            log.info("Не найден фильм: {}.", film);
            throw new NotFoundException("Фильм не найден.");
        }
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        if (filmData.containsKey(id)) {
            filmData.remove(id);
            log.info("Фильм с id = {} удален.", id);
        } else {
            log.info("Фильм с id = {} не найден.", id);
            throw new NotFoundException("Фильм не найден.");
        }
    }

}
