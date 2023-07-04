package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.util.Create;
import ru.practicum.shareit.user.util.Update;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Запрос создания пользователя.");
        return userClient.createUser(userDto);
    }

    @GetMapping("/{id}")
    ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info("Запрос пользователя с id: {}", id);
        return userClient.getUserById(id);
    }

    @GetMapping
    ResponseEntity<Object> getAllUsers() {
        log.info("Запрос всех пользователей.");
        return userClient.getAllUsers();
    }

    @PatchMapping("/{id}")
    ResponseEntity<Object> updateUser(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Запрос обновления пользователя с id: {}", id);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable long id) {
        log.info("Запрос удаления пользователя с id: {}", id);
        return userClient.deleteUser(id);
    }

}
