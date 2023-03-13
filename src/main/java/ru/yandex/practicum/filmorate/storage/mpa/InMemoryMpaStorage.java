package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryMpaStorage implements MpaStorage {

    @Override
    public Mpa findMpa(int id) {
        if (mpaData.containsKey(id)) {
            return mpaData.get(id);
        } else {
            log.info("Рейтинг с id = {} не найден.", id);
            throw new NotFoundException("Рейтинг с id = " + id + " не найден.");
        }
    }

    @Override
    public Collection<Mpa> findAllMpa() {
        return mpaData.values().stream().sorted(Comparator.comparingInt(Mpa::getId)).collect(Collectors.toList());
    }

}
