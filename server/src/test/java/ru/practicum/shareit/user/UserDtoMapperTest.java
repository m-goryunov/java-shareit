package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserDtoMapperTest {
    private final UserDto dto = new UserDto(1L, "User", "user@mail.ru");
    private final User user = new User(1L, "User", "user@mail.ru");

    @Test
    public void toUserDto() {
        UserDto userDto = UserMapper.toUserDto(user);
        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void toUser() {
        User user = UserMapper.fromUserDto(dto);
        assertThat(user.getId(), equalTo(user.getId()));
        assertThat(user.getName(), equalTo(user.getName()));
        assertThat(user.getEmail(), equalTo(user.getEmail()));
    }
}
