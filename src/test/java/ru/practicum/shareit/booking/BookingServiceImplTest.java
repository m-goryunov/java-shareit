package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    BookingServiceImpl underTest;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    UserService userService;

    @MockBean
    ItemRepository itemRepository;

    User user;
    User user2;

    Item item;

    Booking booking;

    @BeforeEach
    void prepare() {
        user = User.builder().id(1L).name("Nikita").email("nikita@mail.ru").build();
        user2 = User.builder().id(2L).name("Mike").email("mike@mail.ru").build();

        item = Item.builder().id(1L)
                .name("Book")
                .description("Good old book")
                .owner(user)
                .available(true)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .booker(user2)
                .build();
    }


    @Test
    void createBooking_EntityNotFoundException_userIdDoesNotExist() {
        Booking request = Booking.builder()
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        doThrow(EntityNotFoundException.class)
                .when(userService).getUserById(anyLong());

        assertThrows(EntityNotFoundException.class, () -> underTest.createBooking(request, 1L, 1L));
    }

    @Test
    void createBooking_EntityNotFoundException_itemDoesNotExist() {
        Booking request = Booking.builder()
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> underTest.createBooking(request, 1L, 1L));
    }

    @Test
    void createBooking_IncorrectRequestException_availableIsFalse() {
        item.setAvailable(false);

        Booking request = Booking.builder()
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(IncorrectRequestException.class, () -> underTest.createBooking(request, 1L, 1L));
    }

    @Test
    void createBooking_EntityNotFoundException_ownerAndRequestorIsSame() {
        Booking request = Booking.builder()
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> underTest.createBooking(request, 1L, 1L));
    }

    @Test
    void createBooking_successfulCreate_requestIsCorrect() {
        Booking request = Booking.builder()
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        underTest.createBooking(request, 2L, 2L);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateStatusById_EntityNotFoundException_bookingDoNotExist() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> underTest.acceptOrRejectBooking(1L, 1L, true));
    }

    @Test
    void updateStatusById_EntityNotFoundException_userIsNotOwnerBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> underTest.acceptOrRejectBooking(1L, 2L, true));
    }

    @Test
    void updateStatusById_IncorrectRequestException_statusIsNotWaiting() {
        booking.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(IncorrectRequestException.class, () -> underTest.acceptOrRejectBooking(1L,1L, true));
    }

    @Test
    void updateStatusById_successfullyUpdatedWithStatusApproved_correctRequest() {
        boolean approved = true;
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking bookingResponse = underTest.acceptOrRejectBooking(1L, 1L, approved);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        assertEquals(BookingStatus.APPROVED, bookingResponse.getStatus());
    }

    @Test
    void updateStatusById_successfullyUpdatedWithStatusRejected_correctRequest() {
        boolean approved = false;
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking bookingResponse = underTest.acceptOrRejectBooking(1L,1L, approved);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        assertEquals(BookingStatus.REJECTED, bookingResponse.getStatus());
    }

    @Test
    void getBookingById_EntityNotFoundException_bookingDoNotExist() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> underTest.getBookingById(1L));
    }

    @Test
    void getBookingById_EntityNotFoundException_userIsNotItemOwnerOrBooker() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> underTest.getBookingById(55L));
    }

    @Test
    void getBookingById_correctResult_requestIsCorrect() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(userService.getUserById(anyLong()))
                .thenReturn(User.builder().id(1L).name("Nikita").email("nikita@mail.ru").build());

        underTest.getBookingById(1L);

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllByBookerId_EntityNotFoundException_userDoNotExist() {
        doThrow(EntityNotFoundException.class)
                .when(userService).getUserById(anyLong());

        assertThrows(EntityNotFoundException.class, () -> underTest.getAllBookingsByUserAndState(1L, "ALL", 0, 10));
    }

    @Test
    void getAllByBookerId_IncorrectRequestException_sizeAndFromAreNotCorrect() {
        int from = -1;
        int size = -1;
        doNothing()
                .when(userService).getUserById(anyLong());

        assertThrows(IncorrectRequestException.class, () -> underTest.getAllBookingsByUserAndState(1L, "ALL", from, size));
    }

    @Test
    void getAllByBookerId_IncorrectRequestException_stateIsIncorrect() {
        String state = "NONE";
        doNothing()
                .when(userService).getUserById(anyLong());

        assertThrows(IncorrectRequestException.class, () -> underTest.getAllBookingsByUserAndState(1L, state, 0, 10));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateAll() {
        String state = "ALL";
        int from = 0;
        int size = 10;
        int page = 0;

        doNothing()
                .when(userService).getUserById(anyLong());

        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());

        underTest.getAllBookingsByUserAndState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByBookerId(1L, pageable);
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateCurrent() {
        String state = "CURRENT";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).getUserById(anyLong());

        underTest.getAllBookingsByUserAndState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStatePast() {
        String state = "PAST";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).getUserById(anyLong());

        underTest.getAllBookingsByUserAndState(1L, state, from, size);

        verify(bookingRepository, atLeast(1))
                .findAllByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateFuture() {
        String state = "FUTURE";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).getUserById(anyLong());

        underTest.getAllBookingsByUserAndState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateWaiting() {
        String state = "WAITING";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).getUserById(anyLong());

        underTest.getAllBookingsByUserAndState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(anyLong(),
                        any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_correctResultAndEmptyList_requestIsCorrectWithStateRejected() {
        String state = "REJECTED";
        int from = 0;
        int size = 10;

        doNothing()
                .when(userService).getUserById(anyLong());

        underTest.getAllBookingsByUserAndState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(anyLong(),
                        any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_EntityNotFoundException_userDoNotExist() {
        doThrow(EntityNotFoundException.class)
                .when(userService).getUserById(anyLong());

        assertThrows(EntityNotFoundException.class,
                () -> underTest.getAllOwnedItemBookingsByState(1L, "ALL", 0, 10));
    }

    @Test
    void getAllByOwnerId_IncorrectRequestException_sizeAndFromNotCorrect() {
        int from = -1;
        int size = -1;

        when(userService.getUserById(anyLong())).thenReturn(user);

        assertThrows(IncorrectRequestException.class,
                () -> underTest.getAllOwnedItemBookingsByState(1L, "ALL", from, size));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateAll() {
        String state = "ALL";
        int from = 0;
        int size = 10;
        int page = 0;

        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));

        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());

        underTest.getAllOwnedItemBookingsByState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerId(item.getId(), pageable);
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateCurrent() {
        String state = "CURRENT";
        int from = 0;
        int size = 10;

        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));


        underTest.getAllOwnedItemBookingsByState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStatePast() {
        String state = "PAST";
        int from = 0;
        int size = 10;

        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));


        underTest.getAllOwnedItemBookingsByState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateFuture() {
        String state = "FUTURE";
        int from = 0;
        int size = 10;

        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));


        underTest.getAllOwnedItemBookingsByState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateWaiting() {
        String state = "WAITING";
        int from = 0;
        int size = 10;

        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));


        underTest.getAllOwnedItemBookingsByState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(anyLong(),
                        any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_correctResultAndEmptyList_requestIsCorrectAndStateRejected() {
        String state = "REJECTED";
        int from = 0;
        int size = 10;

        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));


        underTest.getAllOwnedItemBookingsByState(1L, state, from, size);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(anyLong(),
                        any(BookingStatus.class), any(Pageable.class));
    }
}