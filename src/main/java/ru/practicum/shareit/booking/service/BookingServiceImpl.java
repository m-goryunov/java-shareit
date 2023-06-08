package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;

    @Override
    public Booking createBooking(Booking booking) {
        checkDates(booking.getStart(), booking.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Бронирование не найдено."));
    }

    @Override
    public Booking getBookingByIdForBookerAndOwner(Long id, Long userId) {
        Booking booking = getBookingById(id);
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return booking;
        } else throw new IllegalArgumentException("Просмотреть бронирование может только владелец вещи и/или арендатор");
    }

    @Override
    public Booking acceptOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingById(bookingId);

        if (userId.equals(booking.getItem().getOwner().getId())) {
            if (approved) booking.setStatus(BookingStatus.APPROVED);
            if (!approved) booking.setStatus(BookingStatus.REJECTED);
        } else throw new IllegalArgumentException("Утвердить бронирование может только владелец вещи.");

        return booking;
    }


    private void checkDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Некорректно указаны дата начала и/или окончания");
        }
    }
}
