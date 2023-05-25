package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(User user) {
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto updateUser(User user) {
        return userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }
}
