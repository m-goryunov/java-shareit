package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "ITEMS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "IS_AVAILABLE")
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    private User owner;
    @Transient
    @Nullable
    private Booking lastBooking;
    @Transient
    @Nullable
    private Booking nextBooking;
    @Transient
    @Nullable
    private List<Comment> comments;
    @ManyToOne(targetEntity = ItemRequest.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private ItemRequest request;

    @Nullable
    public Optional<Booking> getLastBooking() {
        return Optional.ofNullable(lastBooking);
    }

    @Nullable
    public Optional<Booking> getNextBooking() {
        return Optional.ofNullable(nextBooking);
    }

    @Nullable
    public Optional<List<Comment>> getComments() {
        return Optional.ofNullable(comments);
    }
}
