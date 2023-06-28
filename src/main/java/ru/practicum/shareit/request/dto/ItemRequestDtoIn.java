package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemRequestDtoIn {
    private Long id;
    @Size(max = 512)
    @NotBlank
    @NotNull
    private String description;

}
