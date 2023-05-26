package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;


    @Override
    public User createUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("Пользователь создан.{}", user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(User user, Long userId) {
        if (users.containsKey(userId) || !isEmailExists(user.getEmail())) {
            users.put(userId, user);
            log.info("Пользователь обновлён.{}", userId);
        } else {
            throw new IllegalArgumentException("Обновление несуществующего пользователя.");
        }
        return users.get(userId);
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
        log.info("Пользователь удалён.{}", id);
    }

    @Override
    public boolean isEmailExists(String email) {
        return users.values().stream().map(User::getEmail).anyMatch(email::equals);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }
}
