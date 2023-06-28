package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest createItemRequest(ItemRequest itemRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден", getClass().getName()));
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(List.of());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest getItemRequestByIdWithResponses(Long requestId) {

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена.", getClass().getName()));

        itemRequest.setItems(itemRepository.findByRequestId(requestId));

        return itemRequest;
    }

    @Override
    public List<ItemRequest> getAllOwnedRequestsWithResponses(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден", getClass().getName()));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);
        List<ItemRequest> requests1 = new ArrayList<>();
        for (ItemRequest request : requests) {
            request.setItems(itemRepository.findByRequestId(request.getId()));
            requests1.add(request);
            //n-обращения к базе?
        }

        return requests1;
    }

    @Override
    @Transactional
    public List<ItemRequest> getAllAvailableItemRequests(Long userId, Long from, Long size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден", getClass().getName()));

        List<ItemRequest> requests = itemRequestRepository.findAllNotByRequestorAndSort(userId);

        for (ItemRequest request : requests) {
            request.setItems(itemRepository.findByRequestId(request.getId()));
            //n-обращения к базе?
        }

        return requests;
    }
}
