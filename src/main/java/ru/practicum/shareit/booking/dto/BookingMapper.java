package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {


    public static BookingResponseDto toDto(Booking booking) {
        UserDto userDto = UserDto.builder()
                .id(booking.getBooker().getId())
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();


        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(userDto)
                .item(itemDto)
                .build();
    }

    public static Booking fromDto(BookingRequestDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }


    public static List<BookingResponseDto> toDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
