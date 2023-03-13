package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Map;

public interface MpaStorage {

    Map<Integer, Mpa> mpaData = Map.of(
            1, Mpa.builder().id(1).name("G").build(),
            2, Mpa.builder().id(2).name("PG").build(),
            3, Mpa.builder().id(3).name("PG-13").build(),
            4, Mpa.builder().id(4).name("R").build(),
            5, Mpa.builder().id(5).name("NC-17").build());

    Mpa findMpa(int id);

    Collection<Mpa> findAllMpa();

}
