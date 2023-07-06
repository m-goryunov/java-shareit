package ru.practicum.shareit.booking.util;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;


    public static Optional<BookingState> checkState(String state) {
        for (BookingState bookingState : values()) {
            if (bookingState.name().equalsIgnoreCase(state))
                return Optional.of(bookingState);
        }
        return Optional.empty();
    }
}