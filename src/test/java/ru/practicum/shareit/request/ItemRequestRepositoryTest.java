package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;


    private final User user = User.builder().name("user").email("user@mail.ru").build();
    private final Item item = Item.builder().name("item").description("cool").available(true).owner(user).build();
    private final User requestor = User.builder().name("user2").email("user2@mail.ru").build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 1, 12, 12, 12))
            .end(LocalDateTime.of(2023, 7, 30, 12, 12, 12))
            .booker(requestor)
            .item(item)
            .status(BookingStatus.WAITING)
            .build();
    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requestor(requestor)
            .created(LocalDateTime.now())
            .build();

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        userRepository.save(requestor);
        itemRepository.save(item);
        bookingRepository.save(booking);
        requestRepository.save(request);
    }

    @Test
    @DirtiesContext
    void findAllByRequestorId() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(2L);

        assertThat(requests.get(0).getId(), equalTo(request.getId()));
        assertThat(requests.size(), equalTo(1));
    }
}