package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKINGS")
@Getter @Setter @ToString
@RequiredArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "START_DATE")
    private LocalDateTime start;
    @Column(name = "END_DATE")
    private LocalDateTime end;
    @NotNull
    private Item item; //??
    @NotNull
    private User booker; //??
    @Enumerated(value = EnumType.STRING)
    private BookingStatus status;
}
