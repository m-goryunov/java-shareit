package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createItemRequest(ItemRequest itemRequest, Long userId);

    ItemRequest getItemRequestByIdWithResponses(Long requestId, Long userId);

    List<ItemRequest> getAllOwnedRequestsWithResponses(Long userId);

    List<ItemRequest> getAllAvailableItemRequests(Long userId, Integer from, Integer size);
}
