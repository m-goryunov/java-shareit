package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;


    private final User user = User.builder().name("user").email("user@mail.ru").id(1L).build();
    private final User booker = User.builder().name("user2").email("user2@mail.ru").id(2L).build();
    private final Item item = Item.builder().description("cool").name("item").available(true).owner(user).id(1L).build();
    private final Booking booking = Booking.builder().id(1L)
            .start(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .end(LocalDateTime.of(2023, 7, 30, 12, 12, 12))
            .item(item)
            .booker(booker)
            .status(BookingStatus.WAITING)
            .build();

    private final Booking bookingDtoIn = Booking.builder().id(1L)
            .start(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .end(LocalDateTime.of(2023, 7, 30, 12, 12, 12))
            .item(Item.builder().id(1L).name("item").build())
            .build();

    private final Booking bookingDtoInWrong = Booking.builder().id(1L)
            .start(LocalDateTime.of(2023, 7, 2, 12, 12, 12))
            .end(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .item(Item.builder().id(1L).name("item").build())
            .build();

    private final Booking bookingDtoWrongItem = Booking.builder().id(1L)
            .start(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .end(LocalDateTime.of(2023, 7, 30, 12, 12, 12))
            .item(Item.builder().id(2L).name("item2").build())
            .build();

    @Test
    void saveNewBooking_whenItemAvailable_thenSavedBooking() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking actualBooking = bookingService.createBooking(bookingDtoIn, 2L, 1L);

        Assertions.assertEquals(booking.getStart(), actualBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), actualBooking.getEnd());
        Assertions.assertEquals(booking.getItem(), actualBooking.getItem());
        Assertions.assertEquals(booking.getBooker(), actualBooking.getBooker());
    }

    @Test
    void saveNewBooking_whenUserNotFound_thenThrownException() {
        when((userRepository).findById(3L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                bookingService.createBooking(bookingDtoIn, 3L, any()));
    }

    @Test
    void saveNewBooking_whenItemNotFound_thenThrownException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when((itemRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                bookingService.createBooking(bookingDtoWrongItem, 2L, 2L));
    }

    @Test
    void saveNewBooking_whenItemNotAvailable_thenThrownException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        item.setAvailable(false);

        Assertions.assertThrows(IncorrectRequestException.class, () ->
                bookingService.createBooking(bookingDtoIn, 2L, 1L));
    }

    @Test
    void saveNewBooking_whenBookerIsOwner_thenThrownException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                bookingService.createBooking(bookingDtoIn, 1L, 1L));
    }

    @Test
    void saveNewBooking_whenIncorrectDatesOfBooking_thenThrownException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Assertions.assertThrows(IncorrectRequestException.class, () ->
                bookingService.createBooking(bookingDtoInWrong, 1L, 1L));
    }

    @Test
    void saveNewBooking_whenOwnerIsBooker_thenThrownException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                bookingService.createBooking(bookingDtoIn, 1L, 1L));
    }

    @Test
    void approve() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        //when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        //when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Booking actualBooking = bookingService.acceptOrRejectBooking(1L, 1L, true);


        Assertions.assertEquals(BookingStatus.APPROVED, actualBooking.getStatus());
    }

    @Test
    void approve_whenBookingNotFound_thenThrownException() {
        //when((bookingRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                bookingService.acceptOrRejectBooking(2L, 1L, true));
    }

    @Test
    void approve_whenItemAlreadyBooked_thenThrownException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.APPROVED);

        Assertions.assertThrows(IncorrectRequestException.class, () ->
                bookingService.acceptOrRejectBooking(1L, 1L, true));
    }

    @Test
    void getBookingById_whenUserIsOwner_thenReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Booking actualBooking = bookingService.getBookingById(1L);

        Assertions.assertEquals(booking, actualBooking);
    }

    @Test
    void getBookingById_whenUserIsNotAuthorOrOwner_thenThrownException() {
        //when(bookingRepository.findById(4L)).thenReturn(Optional.of(booking));
        //when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                bookingService.getBookingById(2L));
    }

    @Test
    void getAllByBooker_whenStateAll_thenReturnAllBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserAndState(2L, "ALL", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByBooker_whenStateCurrent_thenReturnListOfBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserAndState(2L, "CURRENT", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByBooker_whenStatePast_thenReturnListOfBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserAndState(2L, "PAST", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByBooker_whenStateFuture_thenReturnListOfBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserAndState(2L, "FUTURE", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByBooker_whenStateWaiting_thenReturnListOfBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserAndState(2L, "WAITING", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByBooker_whenStateUnsupported_thenExceptionThrown() {
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                bookingService.getAllBookingsByUserAndState(2L, "idi na huy", 0, 10));
    }

    @Test
    void getAllByOwner_whenStateAll_thenReturnAllBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllOwnedItemBookingsByState(1L, "ALL", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);

    }

    @Test
    void getAllByOwner_whenStateCurrent_thenReturnListOfBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllOwnedItemBookingsByState(1L, "CURRENT", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenStatePast_thenReturnListOfBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllOwnedItemBookingsByState(1L, "PAST", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenStateFuture_thenReturnListOfBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllOwnedItemBookingsByState(1L, "FUTURE", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }

    @Test
    void getAllByOwner_whenStateWaiting_thenReturnListOfBookings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllOwnedItemBookingsByState(1L, "WAITING", 0, 10);

        Assertions.assertEquals(List.of(booking), actualBookings);
    }
}