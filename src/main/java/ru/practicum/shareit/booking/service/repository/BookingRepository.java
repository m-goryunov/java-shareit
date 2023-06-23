package ru.practicum.shareit.booking.service.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findAllByBooker_Id(Long userId, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long userId,LocalDateTime end , Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long userId,LocalDateTime start , Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findAllByItem_Owner_Id(Long userId, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long userId,LocalDateTime end , Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long userId,LocalDateTime start , Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findByItemIdAndEndIsBefore(Long itemId, LocalDateTime date);
}
