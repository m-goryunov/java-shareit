package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto getUserById(long id);

    UserDto updateUser(UserDto userDto, Long userId);

    void deleteUser(long id);

    List<UserDto> getAllUsers();
}
