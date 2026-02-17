package ru.practicum.shareit.user.dal;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User saveUser(@NotNull User user) {
        log.info("Запрос на сохранение пользователя: {}", user);
        user = user.toBuilder()
                .id(setId())
                .build();
        log.debug("Пользователю присвоен id: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно сохранен", user);
        return user;
    }

    @Override
    public Optional<User> getUserById(@NotNull long id) {
        log.info("Запрос на получение пользователя по id: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(@NotNull long id) {
        log.info("Запрос на удаление пользователя id: {}", id);
        users.remove(id);
        log.info("Пользователь id: {} успешно удален", id);
    }

    private Long setId() {
        return users.keySet().stream()
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }
}
