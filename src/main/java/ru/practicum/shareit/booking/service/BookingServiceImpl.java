package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(Booking booking) {
        userRepository.findById(userId);
        itemService.getItemById(bookingRequestDto.getItemId());
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
        } else
            throw new IllegalArgumentException("Просмотреть бронирование может только владелец вещи и/или арендатор");
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

    @Override
    public List<Booking> getAllBookingsByUserAndState(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));

        BookingState bookingState = BookingState.checkState(state)
                .orElseThrow(() -> new IllegalArgumentException("State не существует: " + state));

        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByBooker_Id(userId, sortBy);
                break;

            case CURRENT:
                result = bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sortBy);
                break;

            case PAST:
                result = bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), sortBy);
                break;

            case FUTURE:
                result = bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), sortBy);
                break;

            case WAITING:
                result = bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING, sortBy);
                break;

            case REJECTED:
                result = bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.REJECTED, sortBy);
                break;
        }
        return result;
    }

    @Override
    public List<Booking> getAllOwnedItemBookingsByState(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));

        BookingState bookingState = BookingState.checkState(state)
                .orElseThrow(() -> new IllegalArgumentException("State не существует: " + state));

        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByItem_Owner_Id(ownerId, sortBy);
                break;

            case CURRENT:
                result = bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId, LocalDateTime.now(), LocalDateTime.now(), sortBy);
                break;

            case PAST:
                result = bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(ownerId, LocalDateTime.now(), sortBy);
                break;

            case FUTURE:
                result = bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(ownerId, LocalDateTime.now(), sortBy);
                break;

            case WAITING:
                result = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, sortBy);
                break;

            case REJECTED:
                result = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, sortBy);
                break;
        }
        return result;
    }


    private void checkDates(LocalDateTime start, LocalDateTime end) {
        if (end.isAfter(start) || start.isAfter(end) || start.equals(end)) {
            throw new IllegalArgumentException("Некорректно указаны дата начала и/или окончания");
        }
    }
}
