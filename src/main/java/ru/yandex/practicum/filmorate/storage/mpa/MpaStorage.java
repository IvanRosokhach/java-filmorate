package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Map;

public interface MpaStorage {

    Map<Integer, Mpa> mpaData = Map.of(
            1, new Mpa(1, "G"),
            2, new Mpa(2, "PG"),
            3, new Mpa(3, "PG-13"),
            4, new Mpa(4, "R"),
            5, new Mpa(5, "NC-17"));

    Mpa findMpa(int id);

    Collection<Mpa> findAllMpa();

}
