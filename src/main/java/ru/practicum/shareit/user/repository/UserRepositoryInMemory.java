package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepositoryInMemory implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;


}
