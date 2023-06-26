package ru.practicum.shareit.booking.service.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort start);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort by);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort start);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort by);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort start);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort start);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort by);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort start);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort by);

    List<Booking> findAllByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Sort start);

    Boolean existsAllByItemIdAndEndIsBeforeAndBooker_IdEquals(Long itemId, LocalDateTime date, Long bookerId);

    Optional<Booking> findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(long itemId, LocalDateTime now, BookingStatus bookingStatus);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByEndAsc(long itemId, LocalDateTime now, BookingStatus bookingStatus);


    List<Booking> findByItemInAndStartLessThanEqualAndStatusOrderByEndDesc(List<Item> items, LocalDateTime now, BookingStatus status);

    List<Booking> findByItemInAndStartAfterAndStatusOrderByEndAsc(List<Item> items, LocalDateTime now, BookingStatus status);
}