package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto getUserById(long id);

    UserDto updateUser(UserDto user);

    void deleteUser(long id);
}
