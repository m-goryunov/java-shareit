package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {
    Booking createBooking(Booking booking);

    Booking getBookingById(Long id);

    Booking getBookingByIdForBookerAndOwner(Long id, Long userId);

    Booking acceptOrRejectBooking(Long userId,Long bookingId, Boolean approved);
}
