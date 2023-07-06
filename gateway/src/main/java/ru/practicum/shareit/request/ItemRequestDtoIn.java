package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemRequestDtoIn {
    private Long id;
    @Size(max = 512)
    @NotBlank
    private String description;

}
