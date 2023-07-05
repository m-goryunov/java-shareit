package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" SELECT i " +
            "FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')) AND i.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long itemId, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findByRequestIn(List<ItemRequest> requests);
}