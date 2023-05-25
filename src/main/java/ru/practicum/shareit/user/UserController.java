package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/users")
    User createUser(@Valid @RequestBody User user) {
        log.info("Запрос создания пользователя с id: {}", user.getId());
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    User getUserById(@PathVariable long id) {
        log.info("Запрос пользователя с id: {}", id);
        return userService.getUserById(id);
    }

    @PatchMapping
    User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос обновления пользователя с id: {}", user.getId());
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Запрос удаления пользователя с id: {}", id);
        userService.deleteUser(id);
    }

}
