package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemResponseDto toItemDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    public static ItemResponseDto toItemDtoWithBookings(Item item) {


        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingDto(item.getLastBooking()))
                .nextBooking(BookingMapper.toBookingDto(item.getNextBooking()))
                .comments(CommentMapper.toDto(item.getComments()))
                .requestId(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    public static List<ItemResponseDto> toItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static List<ItemResponseDto> toItemDtoWithBookings(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDtoWithBookings)
                .collect(Collectors.toList());
    }

    public static Item fromItemDto(ItemRequestDto itemDto) {

        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemDto.getRequestId())
                .build();

        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemRequest)
                .build();
    }
}
