package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long userId);

    Item updateItemById(Item item, Long itemId, Long userId);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getAllItemsByUserId(Long userId);

    List<Item> searchItem(String text);

    Comment createComment(CommentRequestDto request, Long userId, Long itemId);
}
