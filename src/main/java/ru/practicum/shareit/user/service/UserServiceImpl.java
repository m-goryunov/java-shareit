package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));
    }

    @Override
    @Transactional
    public User updateUser(User user, Long userId) {
        User existUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь для обновления не найден.", getClass().toString()));

        if (user.getEmail() != null && !user.getEmail().isEmpty() && !Objects.equals(existUser.getEmail(), user.getEmail())) {
            existUser.setEmail(user.getEmail());
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            existUser.setName(user.getName());
        }
        return existUser;
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
