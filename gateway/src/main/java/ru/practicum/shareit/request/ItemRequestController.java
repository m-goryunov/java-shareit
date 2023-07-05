package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        log.info("Запрос создания запроса вещи {} у пользователя {}", itemRequestDtoIn.getId(), userId);
        return requestClient.createItemRequest(itemRequestDtoIn, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestByIdWithResponses(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                                  @PathVariable Long requestId) {
        log.info("Просмотр запроса вещи {}", requestId);
        return requestClient.getItemRequestByIdWithResponses(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnedRequestsWithResponses(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос заявок на вещи у пользователя-владельца {}", userId);
        return requestClient.getAllOwnedRequestsWithResponses(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllAvailableItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                              @RequestParam(name = "from", defaultValue = "0")
                                                              @PositiveOrZero Integer from,
                                                              @RequestParam(name = "size", defaultValue = "10")
                                                              @Positive Integer size) {
        log.info("Запрос заявок всех заявок на вещи, кроме своих {}", userId);
        return requestClient.getAllAvailableItemRequests(from, size, userId);
    }
}
