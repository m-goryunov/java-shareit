package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Sql("classpath:data.sql")
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void findByBookerIdAndEndIsBefore_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 25, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Booking> result = bookingRepository.findAllByBookerIdAndEndIsBefore(3L, date, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByBookerIdAndEndIsBefore_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 25, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Booking> result = bookingRepository.findAllByBookerIdAndEndIsBefore(1L, date, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByBookerId_notEmptyResult_bookingExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findAllByBookerId(3L, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findAllByBookerId_emptyResult_bookingDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findAllByBookerId(1L, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfter_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(3L, date, date, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfter_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(1L, date, date, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findByBookerIdAndStartIsAfter_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 8, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsAfter(3L, date, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByBookerIdAndStartIsAfter_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 8, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsAfter(1L, date, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByItemIdIn_notEmptyResult_bookingExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findAllByItemOwnerId(1L, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findAllByItemIdIn_emptyResult_bookingDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerId(3L, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInAndStartIsBeforeAndEndIsAfter_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(2L, date, date, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByItemIdInAndStartIsBeforeAndEndIsAfter_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(3L, date, date, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInAndEndIsBefore_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(2L, date, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByItemIdInAndEndIsBefore_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(3L, date, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInAndStartIsAfterAndStatusIs_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStatus(1L, BookingStatus.APPROVED, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByItemIdInAndStartIsAfterAndStatusIs_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStatus(3L, BookingStatus.WAITING, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findByBookerIdAndStartIsAfterAndStatusIs_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStatus(3L, BookingStatus.APPROVED, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByBookerIdAndStartIsAfterAndStatusIsdStatusIs_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStatus(1L, BookingStatus.APPROVED, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInAndStartIsAfter_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartIsAfter(1L, date, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findLastBooking_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        Booking result = bookingRepository
                .findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(1L, date, BookingStatus.APPROVED).get();

        assertThat(result).isNotNull();
    }

    @Test
    void findLastBooking_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        Booking result = bookingRepository
                .findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(1L, date, BookingStatus.APPROVED).get();

        assertThat(result).isNotNull();
    }

    @Test
    void findNextBooking_notEmptyResult_ownerItemIsCorrect() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemInAndStartAfterAndStatusOrderByEndAsc(List.of(new Item()), date, BookingStatus.APPROVED, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat( result.size() == 1);
    }

}