package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
public class InMemoryGenreStorage implements GenreStorage {

    @Override
    public Genre findGenre(int id) {
        if (genreData.containsKey(id)) {
            return genreData.get(id);
        } else {
            throw new NotFoundException("Такого жанра нет");
        }
    }

    @Override
    public Collection<Genre> findAllGenres() {
        return genreData.values().stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());
    }

}
