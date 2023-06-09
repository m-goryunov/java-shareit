package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        Booking booking = BookingMapper.fromDto(bookingDto,
                userService.getUserById(userId),
                itemService.getItemById(bookingDto.getItem()));

        return BookingMapper.toDto(bookingService.createBooking(booking));
    }

    //Подтверждение или отклонение запроса на бронирование. Только для owner
    @PatchMapping("/{bookingId}?approved={approved}")
    public BookingDto acceptOrRejectBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                            @PathVariable Long bookingId, @PathVariable Boolean approved) {
        return BookingMapper.toDto(bookingService.acceptOrRejectBooking(userId, bookingId, approved));
    }

    //Получение данных о конкретном бронировании (включая его статус). Только для booker и owner
    @GetMapping("/{bookingId}")
    public BookingDto getBookingByIdForBookerAndOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        return BookingMapper.toDto(bookingService.getBookingByIdForBookerAndOwner(bookingId, userId));
    }

    //Получение списка всех бронирований текущего пользователя.
    //CURRENT PAST FUTURE WAITING REJECTED Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    @GetMapping(params = "state")
    public List<BookingDto> getAllBookingsByUserAndState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @RequestParam(required = false, defaultValue = "ALL") String state) {

    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    @GetMapping(params = {"owner", "state"})
    public List<BookingDto> getAllOwnedItemBookingsByState(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @RequestParam Long ownerId,
                                                    @RequestParam(required = false, defaultValue = "ALL") String state) {

    }
}
