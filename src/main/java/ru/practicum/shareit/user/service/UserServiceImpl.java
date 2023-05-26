package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

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
                -> new EntityNotFoundException("Пользователь не найден.", getClass().toString())));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь для обновления не найден.", getClass().toString()));

        if (userDto.getEmail() != null && !Objects.equals(userDto.getEmail(), user.getEmail())) {
            isEmailExists(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.updateUser(user, userId));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDto(userRepository.getAllUsers());
    }

    private void isEmailExists(String email) {
        if (userRepository.isEmailExists(email)) {
            throw new IllegalArgumentException("Пользователь с указанным email уже существует.");
        }
    }
}
