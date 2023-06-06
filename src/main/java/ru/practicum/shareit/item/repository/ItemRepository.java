package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item createItem(Item item);

    void updateItemById(Item item);

    Optional<Item> getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    List<Item> searchItem(String text);
}
