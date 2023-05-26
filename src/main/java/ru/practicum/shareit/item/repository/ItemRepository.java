package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    void updateItemById(Item item);

    Optional<Item> getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    List<Item> searchItem(String text);
}
