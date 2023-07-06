package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemRequestDto itemDto) {
        log.info("Запрос создания вещи {} у пользователя {}", itemDto.getId(), userId);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody ItemRequestDto itemDto) {
        log.info("Запрос редактирования вещи {}", itemDto.getId());
        return itemClient.updateItemById(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        log.info("Запрос  вещи {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос всех вещей по id пользователя: {}", userId);
        return itemClient.getAllItemsByUserId(from, size, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(name = "text") String text,
                                             @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос поиска вещи по тексту: {}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return itemClient.searchItem(from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentRequestDto commentRequestDto) {

        return itemClient.createComment(itemId, commentRequestDto, userId);
    }

}
