package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;

    @BeforeEach
    void prepare() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY = 0; " +
                "TRUNCATE TABLE bookings, items, users RESTART IDENTITY;" +
                "SET REFERENTIAL_INTEGRITY = 1;");
    }

    @Test
    void createBooking_EntityNotFoundException_correctRequest() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking result = bookingService.createBooking(request,savedUser2.getId(), savedItem.getId());

        assertThat(result).isNotNull();
        assertThat(result.getItem().getName()).isEqualTo(savedItem.getName());
    }

    @Test
    void createBooking_EntityNotFoundException_itemNotFound() {

        Item savedItem = itemService.createItem(Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true).build(), 1L);

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(request, 1L, 1L));
    }

    @Test
    void createBooking_IncorrectRequestException_itemIsNotAvailable() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(false)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        assertThrows(IncorrectRequestException.class, () -> bookingService.createBooking(request, savedUser2.getId(), savedItem.getId()));
    }

    @Test
    void createBooking_EntityNotFoundException_ownerBookingItem() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(false)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        assertThrows(IncorrectRequestException.class, () -> bookingService.createBooking(request, savedUser1.getId(), savedItem.getId()));
    }

    @Test
    void updateStatusById_EntityNotFoundException_bookingDoesNotExist() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking booking = bookingService.createBooking(request, savedUser2.getId(), savedItem.getId());

        assertThrows(EntityNotFoundException.class, () -> bookingService.acceptOrRejectBooking(savedUser1.getId(),10L, false));
    }

    @Test
    void updateStatusById_EntityNotFoundException_userCanNotChangeStatus() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking savedBooking = bookingService.createBooking(request, savedUser2.getId(), savedItem.getId());

        assertThrows(EntityNotFoundException.class, () -> bookingService.acceptOrRejectBooking(savedUser2.getId(), savedBooking.getId(), false));
    }

    @Test
    void updateStatusById_npExceptions_correctRequest() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking savedBooking = bookingService.createBooking(request, savedUser2.getId(), savedItem.getId());

        assertDoesNotThrow(() -> bookingService.acceptOrRejectBooking( savedUser1.getId(), savedBooking.getId(), false));

    }

    @Test
    void updateStatusById_EntityNotFoundException_statusIsNotWaiting() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking savedBooking = bookingService.createBooking(request, savedUser2.getId(), savedItem.getId());

        bookingService.acceptOrRejectBooking(savedUser1.getId(),savedBooking.getId(), false);

        assertThrows(IncorrectRequestException.class,
                () -> bookingService.acceptOrRejectBooking( savedUser1.getId(), savedBooking.getId(), false));
    }

    @Test
    void getBookingById_EntityNotFoundException_bookingDoesNotExist() {
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(1L));
    }

    @Test
    void getBookingById_EntityNotFoundException_userIsNotOwnerBooking() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();
        User userRequest3 = User.builder().name("Sam").email("sam@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);
        User savedUser3 = userService.createUser(userRequest3);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking savedBooking = bookingService.createBooking(request, savedUser2.getId(), savedItem.getId());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(savedBooking.getId()));
    }

    @Test
    void getBookingById_correctBooking_requestIsCorrect() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking savedBooking = bookingService.createBooking(request, savedUser2.getId(), savedItem.getId());

        Booking result = bookingService.getBookingById(savedBooking.getId());

        assertThat(result.getItem().getName()).isEqualTo(itemRequest.getName());
        assertThat(result.getBooker().getName()).isEqualTo(savedUser2.getName());
    }

    @Test
    void getAllByBookerId_emptyResultList_requestIsCorrect() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking savedBooking = bookingService.createBooking(request, savedUser2.getId(), savedItem.getId());

        List<Booking> result = bookingService.getAllBookingsByUserAndState(savedUser2.getId(), BookingStatus.WAITING.toString(), 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllByOwnerId_emptyResultList_requestIsCorrect() {
        User userRequest1 = User.builder().name("Nikita").email("nikita@mail.ru").build();
        User userRequest2 = User.builder().name("Tom").email("tom@mail.ru").build();

        User savedUser1 = userService.createUser(userRequest1);
        User savedUser2 = userService.createUser(userRequest2);

        Item itemRequest = Item.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Item savedItem = itemService.createItem(itemRequest, savedUser1.getId());

        Booking request = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 2, 10, 17, 10, 5))
                .end(LocalDateTime.of(2023, 2, 10, 17, 10, 5).plusDays(15))
                .build();

        Booking savedBooking = bookingService.createBooking(request, savedUser2.getId(), savedItem.getId());

        List<Booking> result = bookingService.getAllOwnedItemBookingsByState(savedUser2.getId(), BookingStatus.WAITING.toString(), 0, 10);

        assertThat(result).isEmpty();
    }
}