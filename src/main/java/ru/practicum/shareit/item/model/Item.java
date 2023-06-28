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
import java.util.Objects;
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
    @Nullable
    @ManyToOne(targetEntity = ItemRequest.class)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(available, item.available) && Objects.equals(owner, item.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner);
    }

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
