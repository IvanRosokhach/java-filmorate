//package ru.yandex.practicum.filmorate.storage.film;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
//import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
//import ru.yandex.practicum.filmorate.storage.user.UserStorage;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collector;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
////@Primary
//public class InMemoryFilmStorage implements FilmStorage {
//
//    private final UserStorage userStorage;
//    private final GenreStorage genreStorage;
//    private final MpaStorage mpaStorage;
//
//    public InMemoryFilmStorage(UserStorage userStorage, GenreStorage genreStorage, MpaStorage mpaStorage) {
//        this.userStorage = userStorage;
//        this.genreStorage = genreStorage;
//        this.mpaStorage = mpaStorage;
//    }
//
//    private long idGenerator = 1;
//
//    @Override
//    public Film findFilm(long id) {
//        if (filmData.containsKey(id)) {
//            return filmData.get(id);
//        } else {
//            log.info("Фильм с id = {} не найден.", id);
//            throw new NotFoundException("Фильм с id = " + id + " не найден.");
//        }
//    }
//
//    @Override
//    public Collection<Film> findAllFilms() {
//        log.info("Текущее количество фильмов: {}.", filmData.size());
//        return filmData.values();
//    }
//
//    @Override
//    public Film createFilm(Film film) {
//        film.setId(idGenerator++);
//        filmData.put(film.getId(), film);
//        log.info("Фильм добавлен: {}.", film);
////        if (film.getMpa() != null) {
////            film.getMpa().setName(mpaStorage.findMpa(film.getMpa().getId()).getName());
////        }
//        return film;
//    }
//
//    @Override
//    public Film updateFilm(Film film) {
//        if (filmData.containsKey(film.getId())) {
//            filmData.put(film.getId(), film);
//            log.info("Фильм обновлен: {}.", film);
//        } else {
//            log.info("Не найден фильм: {}.", film);
//            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден.");
//        }
//        return film;
//    }
//
//    @Override
//    public void deleteFilm(long id) {
//        if (filmData.containsKey(id)) {
//            filmData.remove(id);
//            log.info("Фильм с id = {} удален.", id);
//        } else {
//            log.info("Фильм с id = {} не найден.", id);
//            throw new NotFoundException("Фильм с id = " + id + " не найден.");
//        }
//    }
//
//    @Override
//    public void addLike(long id, long userId) {
//        findFilm(id).getLikes().add(userStorage.findUser(userId).getId());
//        log.info("Лайк добавлен.");
//    }
//
//    @Override
//    public void deleteLike(long id, long userId) {
//        findFilm(id).getLikes().remove(userStorage.findUser(userId).getId());
//        log.info("Лайк удален.");
//    }
//
//    @Override
//    public List<Film> findPopularFilms(int count) {
//        return findAllFilms().stream()
//                .sorted(this::compare)
//                .limit(count)
//                .collect(Collectors.toList());
//    }
//
//    private int compare(Film film0, Film film1) {
//        return -1 * (film0.getLikes().size() - film1.getLikes().size());
//    }
//
//}
