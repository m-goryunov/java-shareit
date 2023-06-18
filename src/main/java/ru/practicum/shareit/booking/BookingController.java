package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

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
        return BookingMapper.toDto(bookingService.createBooking(booking));
    }

    //Подтверждение или отклонение запроса на бронирование. Только для owner
    @PatchMapping("/{bookingId}")
    public BookingDto acceptOrRejectBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        return BookingMapper.toDto(bookingService.acceptOrRejectBooking(userId, bookingId, approved));
    }

    //Получение данных о конкретном бронировании (включая его статус). Только для booker и owner
    @GetMapping("/{bookingId}")
    public BookingDto getBookingByIdForBookerAndOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long bookingId) {
        return BookingMapper.toDto(bookingService.getBookingByIdForBookerAndOwner(bookingId, userId));
    }

    @GetMapping(params = "state")
    public List<BookingDto> getAllBookingsByUserAndState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                         @RequestParam(required = false, defaultValue = "ALL") String state) {
        return BookingMapper.toDto(bookingService.getAllBookingsByUserAndState(userId, state));
    }

    @GetMapping(params = {"owner", "state"})
    public List<BookingDto> getAllOwnedItemBookingsByState(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @RequestParam Long ownerId,
                                                           @RequestParam(required = false, defaultValue = "ALL") String state) {
        return BookingMapper.toDto(bookingService.getAllOwnedItemBookingsByState(userId, state));
    }
}
