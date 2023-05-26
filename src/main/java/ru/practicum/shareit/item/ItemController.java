package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос создания вещи {} у пользователя {}", itemDto.getId(), userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        log.info("Запрос редактирования вещи {}", itemDto.getId());
        return itemService.updateItemById(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Запрос  вещи {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос всех вещей по id пользователя: {}", userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text) {
        log.info("Запрос поиска вещи по тексту: {}", text);
        return itemService.searchItem(text);
    }

}
