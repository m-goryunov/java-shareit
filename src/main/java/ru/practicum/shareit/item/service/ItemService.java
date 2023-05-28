package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long userId);

    Item updateItemById(Item item, Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    List<Item> searchItem(String text);
}
