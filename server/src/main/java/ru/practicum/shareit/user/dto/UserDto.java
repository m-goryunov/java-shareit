package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;


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
