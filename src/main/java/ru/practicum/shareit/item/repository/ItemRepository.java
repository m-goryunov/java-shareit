package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" SELECT i " +
            "FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')) AND i.available = true")
    List<Item> search(String text);

    List<Item> findAllByOwnerId(Long itemId);

    List<Item> findByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(Set<Long> ids);
}
