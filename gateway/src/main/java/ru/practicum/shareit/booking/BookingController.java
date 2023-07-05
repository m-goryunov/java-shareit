package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Validated BookingRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(requestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> acceptOrRejectBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                        @PathVariable Long bookingId,
                                                        @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.acceptOrRejectBooking(bookingId, approved, userId);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnedItemBookingsByState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                                 @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                                 @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return bookingClient.getAllOwnedItemBookingsByState(from, size, state, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserAndState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                               @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                               @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                               @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return bookingClient.getAllBookingsByUserAndState(from, size, state, userId);
    }
}
