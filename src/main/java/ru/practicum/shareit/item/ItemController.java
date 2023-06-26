package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemRequestDto itemDto) {
        log.info("Запрос создания вещи {} у пользователя {}", itemDto.getId(), userId);
        Item item = ItemMapper.fromItemDto(itemDto);
        return ItemMapper.toItemDto(itemService.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemRequestDto itemDto) {
        log.info("Запрос редактирования вещи {}", itemDto.getId());
        Item item = ItemMapper.fromItemDto(itemDto);
        return ItemMapper.toItemDto(itemService.updateItemById(item, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId) {
        log.info("Запрос  вещи {}", itemId);
        return ItemMapper.toItemDtoWithBookings(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public List<ItemResponseDto> getAllItemsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос всех вещей по id пользователя: {}", userId);
        return ItemMapper.toItemDtoWithBookings(itemService.getAllItemsByUserId(userId));
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItem(@RequestParam(name = "text") String text) {
        log.info("Запрос поиска вещи по тексту: {}", text);
        return ItemMapper.toItemDto(itemService.searchItem(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId,
                                            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        Comment comment = CommentMapper.fromDto(commentRequestDto);

        return CommentMapper.toDto(itemService.createComment(comment, userId, itemId));
    }

}
