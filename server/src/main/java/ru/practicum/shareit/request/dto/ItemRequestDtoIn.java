package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDtoIn {
    private Long id;
    private String description;

}
