package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long userId);

    Item updateItemById(Item item, Long itemId, Long userId);

    Item getItemById(Long itemId, Long userId);

    List<Item> getAllItemsByUserId(Long userId, Integer from, Integer size);

    List<Item> searchItem(String text, Integer from, Integer size);

    Comment createComment(Comment request, Long userId, Long itemId);
}
