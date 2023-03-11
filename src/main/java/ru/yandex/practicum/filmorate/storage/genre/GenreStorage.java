package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;

public interface GenreStorage {

    Map<Integer, Genre> genreData = Map.of(
            1, Genre.builder().id(1).name("Комедия").build(),
            2, Genre.builder().id(2).name("Драма").build(),
            3, Genre.builder().id(3).name("Мультфильм").build(),
            4, Genre.builder().id(4).name("Триллер").build(),
            5, Genre.builder().id(5).name("Документальный").build(),
            6, Genre.builder().id(6).name("Боевик").build());

    Genre findGenre(int id);

    Collection<Genre> findAllGenres();

}
