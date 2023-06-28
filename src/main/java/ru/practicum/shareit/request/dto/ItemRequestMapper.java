package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest fromDto(ItemRequestDtoIn itemRequestDtoIn) {
        return ItemRequest.builder()
                .id(itemRequestDtoIn.getId())
                .description(itemRequestDtoIn.getDescription())
                .build();
    }

    public static ItemRequestDtoOut toDto(ItemRequest itemRequest) {
        return ItemRequestDtoOut.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemMapper.toItemDto(itemRequest.getItems().get()))
                .build();
    }

    public static List<ItemRequestDtoOut> toDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }
}
