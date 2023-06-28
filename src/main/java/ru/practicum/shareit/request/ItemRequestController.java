package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOut createItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                               @Valid @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        log.info("Запрос создания запроса вещи {} у пользователя {}", itemRequestDtoIn.getId(), userId);
        ItemRequest itemRequest = ItemRequestMapper.fromDto(itemRequestDtoIn);
        return ItemRequestMapper.toDto(itemRequestService.createItemRequest(itemRequest, userId));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getItemRequestByIdWithResponses(@PathVariable Long requestId) {
        log.info("Просмотр запроса вещи {}", requestId);
        return ItemRequestMapper.toDto(itemRequestService.getItemRequestByIdWithResponses(requestId));
    }

    @GetMapping
    public List<ItemRequestDtoOut> getAllOwnedRequestsWithResponses(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос заявок на вещи у пользователя-владельца {}", userId);
        return ItemRequestMapper.toDto(itemRequestService.getAllOwnedRequestsWithResponses(userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllAvailableItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                               @RequestParam(name = "from", defaultValue = "0") Long from,
                                                               @RequestParam(name = "size", defaultValue = "10") Long size) {
        log.info("Запрос заявок всех заявок на вещи, кроме своих {}", userId);
        return ItemRequestMapper.toDto(itemRequestService.getAllAvailableItemRequests(userId, from, size));
    }
}
