package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Validated(Create.class)
    UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос создания пользователя.");
        return userService.createUser(userDto);
    }

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable long id) {
        log.info("Запрос пользователя с id: {}", id);
        return userService.getUserById(id);
    }

    @GetMapping
    List<UserDto> getAllUsers() {
        log.info("Запрос всех пользователей.");
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    UserDto updateUser(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Запрос обновления пользователя с id: {}", id);
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Запрос удаления пользователя с id: {}", id);
        userService.deleteUser(id);
    }

}
