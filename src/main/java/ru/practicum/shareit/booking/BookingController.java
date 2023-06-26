package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingRequestDto bookingRequestDto) {

        Booking booking = BookingMapper.fromDto(bookingRequestDto);

        return BookingMapper.toDto(bookingService.createBooking(booking, userId, bookingRequestDto.getItemId()));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto acceptOrRejectBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long bookingId,
                                                    @RequestParam(name = "approved") Boolean approved) {
        return BookingMapper.toDto(bookingService.acceptOrRejectBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingByIdForBookerAndOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                              @PathVariable Long bookingId) {
        return BookingMapper.toDto(bookingService.getBookingByIdForBookerAndOwner(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsByUserAndState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return BookingMapper.toDto(bookingService.getAllBookingsByUserAndState(userId, state));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnedItemBookingsByState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                                   @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return BookingMapper.toDto(bookingService.getAllOwnedItemBookingsByState(userId, state));
    }
}

