package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена.", getClass().toString()));

        if (!item.getAvailable()) {
            throw new IncorrectRequestException("Вещь недоступна для бронирования.", getClass().toString());
        }

        checkDates(bookingRequestDto.getStart(), bookingRequestDto.getEnd());

        if (Objects.equals(item.getOwner().getId(), userId))
            throw new EntityNotFoundException("Невозможно забронировать вещь у самого себя.", getClass().toString());

        Booking booking = BookingMapper.fromDto(bookingRequestDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Бронирование не найдено.", getClass().toString()));
    }

    @Override
    public Booking getBookingByIdForBookerAndOwner(Long id, Long userId) {
        Booking booking = getBookingById(id);
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return booking;
        } else
            throw new EntityNotFoundException("Просмотреть бронирование может только владелец вещи и/или арендатор",
                    getClass().toString());
    }

    @Override
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

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookingsByUserAndState(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));

        BookingState bookingState = BookingState.checkState(state)
                .orElseThrow(() ->
                        new IncorrectRequestException(String.format("Unknown state: " + state), getClass().toString()));

        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByBookerId(userId, sortBy);
                break;

            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), sortBy);
                break;

            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortBy);
                break;

            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortBy);
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
    public List<Booking> getAllOwnedItemBookingsByState(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));

        BookingState bookingState = BookingState.checkState(state)
                .orElseThrow(() -> new IncorrectRequestException(String.format("Unknown state: " + state),
                        getClass().toString()));

        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerId(ownerId, sortBy);
                break;

            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId,
                        LocalDateTime.now(), LocalDateTime.now(), sortBy);
                break;

            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now(), sortBy);
                break;

            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now(), sortBy);
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
