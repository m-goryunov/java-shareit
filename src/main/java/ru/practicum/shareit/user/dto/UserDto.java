package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email(groups = {Create.class, Update.class}, regexp = "[\\w._]{1,10}@[\\w]{2,}.[\\w]{2,}")
    @NotEmpty(groups = Create.class)
    private String email;
}
