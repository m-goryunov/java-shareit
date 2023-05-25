package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(User user);

    UserDto getUserById(long id);

    User updateUser(User user);

    void deleteUser(long id);
}
