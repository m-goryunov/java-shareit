package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public Item createItem(Item item, Long userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден.", getClass().toString()));
        item.setOwner(user);
        return itemRepository.createItem(item);
    }

    @Override
    public Item updateItemById(Item itemDto, Long itemId, Long userId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("Не совпадают владельцы вещей.", getClass().toString());
        }

        if (StringUtils.hasText(itemDto.getName())) {
            item.setName(itemDto.getName());
        }

        if (StringUtils.hasText(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }

        if ((itemDto.getAvailable() != null)) {
            item.setAvailable(itemDto.getAvailable());
        }

        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        return itemRepository.getAllItemsByUserId(userId);
    }

    @Override
    public List<Item> searchItem(String text) {
        if (!StringUtils.hasLength(text)) {
            return List.of();
        }
        return itemRepository.searchItem(text);
    }
}
