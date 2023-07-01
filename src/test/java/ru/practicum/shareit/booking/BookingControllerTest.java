package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;


    private final BookingRequestDto bookingDto = BookingRequestDto.builder().itemId(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2)).build();

    private final BookingRequestDto bookingNullStart = BookingRequestDto.builder().itemId(1L)
            .start(null)
            .end(LocalDateTime.now().plusDays(2)).build();

    private final Booking booking = Booking.builder().id(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(Item.builder().id(1L).name("item").build())
            .booker(User.builder().id(1L).name("user").build())
            .build();


    @Test
    void saveNewBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong(), anyLong())).thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(BookingMapper.toDto(booking))));
    }

    @Test
    void saveNewBooking_whenNoStart_thenThrownException() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingNullStart))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approve() throws Exception {
        when(bookingService.acceptOrRejectBooking(anyLong(), any(), any())).thenReturn(booking);
        booking.setStatus(BookingStatus.APPROVED);

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(BookingMapper.toDto(booking))));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingByIdForBookerAndOwner(1L, 1L)).thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(BookingMapper.toDto(booking))));
    }

    @Test
    void getAllByBooker() throws Exception {
        when(bookingService.getAllBookingsByUserAndState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings?state=ALL")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(BookingMapper.toDto(booking)))));
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllOwnedItemBookingsByState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner?state=ALL")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(BookingMapper.toDto(booking)))));
    }
}