package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking, Long userId, Long itemId);

    Booking getBookingById(Long id);

    Booking getBookingByIdForBookerAndOwner(Long id, Long userId);

    Booking acceptOrRejectBooking(Long userId, Long bookingId, Boolean approved);

    List<Booking> getAllBookingsByUserAndState(Long userId, String state, Integer from, Integer size);

    List<Booking> getAllOwnedItemBookingsByState(Long userId, String state, Integer from, Integer size);
}
