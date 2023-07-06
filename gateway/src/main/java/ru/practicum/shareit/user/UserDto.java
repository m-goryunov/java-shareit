package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.user.util.Create;
import ru.practicum.shareit.user.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @Email(groups = {Create.class, Update.class}, regexp = "[\\w._]{1,10}@[\\w]{2,}.[\\w]{2,}")
    @NotEmpty(groups = Create.class)
    private String email;
}
