package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking createBooking(Booking booking, Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена.", getClass().toString()));

        if (!item.getAvailable()) {
            throw new IncorrectRequestException("Вещь недоступна для бронирования.", getClass().toString());
        }

        checkDates(booking.getStart(), booking.getEnd());

        if (Objects.equals(item.getOwner().getId(), userId))
            throw new EntityNotFoundException("Невозможно забронировать вещь у самого себя.", getClass().toString());

        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Бронирование не найдено.", getClass().toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingByIdForBookerAndOwner(Long id, Long userId) {
        Booking booking = getBookingById(id);
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return booking;
        } else
            throw new EntityNotFoundException("Просмотреть бронирование может только владелец вещи и/или арендатор",
                    getClass().toString());
    }

    @Override
    @Transactional
    public Booking acceptOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено.", getClass().toString()));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId))
            throw new EntityNotFoundException("Нельзя забронировать у самого себя.", getClass().toString());

        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new IncorrectRequestException("Статус должен быть WAITING.", getClass().toString());

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsByUserAndState(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));

        BookingState bookingState = BookingState.checkState(state)
                .orElseThrow(() ->
                        new IncorrectRequestException(String.format("Unknown state: " + state), getClass().toString()));

        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = List.of();

        final LocalDateTime NOW = LocalDateTime.now();

        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByBookerId(userId, sortBy);
                break;

            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                        NOW, NOW, sortBy);
                break;

            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, NOW, sortBy);
                break;

            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, NOW, sortBy);
                break;

            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, sortBy);
                break;

            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, sortBy);
                break;
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllOwnedItemBookingsByState(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));

        BookingState bookingState = BookingState.checkState(state)
                .orElseThrow(() -> new IncorrectRequestException(String.format("Unknown state: " + state),
                        getClass().toString()));

        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = List.of();

        final LocalDateTime NOW = LocalDateTime.now();

        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerId(ownerId, sortBy);
                break;

            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId,
                        NOW, NOW, sortBy);
                break;

            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(ownerId, NOW, sortBy);
                break;

            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(ownerId, NOW, sortBy);
                break;

            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sortBy);
                break;

            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sortBy);
                break;
        }
        return result;
    }


    private void checkDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start) || start.equals(end)) {
            throw new IncorrectRequestException("Некорректно указаны дата начала и/или окончания", getClass().toString());
        }
    }
}
