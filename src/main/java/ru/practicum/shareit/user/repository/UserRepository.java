package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

public interface UserRepository {
    User createUser(User user);

    User getUserById(long id);

    User updateUser(User user);

    void deleteUser(long id);
}
