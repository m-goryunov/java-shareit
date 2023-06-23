package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
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
    public ItemDto createItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос создания вещи {} у пользователя {}", itemDto.getId(), userId);
        Item item = ItemMapper.fromItemDto(itemDto);
        return ItemMapper.toItemDto(itemService.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        log.info("Запрос редактирования вещи {}", itemDto.getId());
        Item item = ItemMapper.fromItemDto(itemDto);
        return ItemMapper.toItemDto(itemService.updateItemById(item, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Запрос  вещи {}", itemId);
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос всех вещей по id пользователя: {}", userId);
        return ItemMapper.toItemDto(itemService.getAllItemsByUserId(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text) {
        log.info("Запрос поиска вещи по тексту: {}", text);
        return ItemMapper.toItemDto(itemService.searchItem(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                            @Valid @RequestBody CommentRequestDto request) {
        return itemService.createComment(request, userId, itemId);
    }

}
