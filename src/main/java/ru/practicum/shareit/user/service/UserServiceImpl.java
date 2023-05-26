package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.isEmailExists(userDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с указанным email уже существует.");
        }
        User user = UserMapper.fromUserDto(userDto);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id).orElseThrow(()
                -> new IllegalArgumentException("Фильм/Пользователь не найден.")));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        if (userRepository.isEmailExists(userDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с указанным email уже существует.");
        }
        return UserMapper.toUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }
}
