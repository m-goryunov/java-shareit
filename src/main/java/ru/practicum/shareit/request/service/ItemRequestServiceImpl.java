package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
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
@Transactional
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
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest getItemRequestByIdWithResponses(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден", getClass().getName()));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена.", getClass().getName()));

        itemRequest.setItems(itemRepository.findAllByRequestId(requestId));

        return itemRequest;
    }

    @Override
    public List<ItemRequest> getAllOwnedRequestsWithResponses(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден", getClass().getName()));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(userId);

        return addItems(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequest> getAllAvailableItemRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден", getClass().getName()));

        int page = from == 0 ? 0 : (from / size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdIsNot(userId, pageable);

        return addItems(requests);
    }

    private List<ItemRequest> addItems(List<ItemRequest> requests) {
        final List<ItemRequest> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> items = new ArrayList<>(itemRepository.findAllByRequestId(request.getId()));
            request.setItems(items);
            result.add(request);
        }
        return result;
    }
}
