package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        isEmailExists(user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));
    }

    @Override
    public User updateUser(User user, Long userId) {
        User existUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь для обновления не найден.", getClass().toString()));

        if (user.getEmail() != null && !user.getEmail().isEmpty() && !Objects.equals(existUser.getEmail(), user.getEmail())) {
            isEmailExists(user.getEmail());
            existUser.setEmail(user.getEmail());
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            existUser.setName(user.getName());
        }

        return existUser;
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private void isEmailExists(String email) {
        if (userRepository.findAll().stream().map(User::getEmail).anyMatch(email::equals)) {
            throw new IllegalArgumentException("Пользователь с указанным email уже существует.");
        }
    }
}
