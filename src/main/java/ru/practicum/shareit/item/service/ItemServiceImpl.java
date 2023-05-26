package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;


    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User user = UserMapper.fromUserDto(userService.getUserById(userId));
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItemById(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new IllegalArgumentException("Не совпадают владельцы вещей.");
        }

        if (StringUtils.hasLength(itemDto.getName())) {
            item.setName(itemDto.getName());
        }

        if (StringUtils.hasLength(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }

        if ((itemDto.getAvailable() != null)) {
            item.setAvailable(itemDto.getAvailable());
        }

        itemRepository.updateItemById(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        List<Item> items = itemRepository.getAllItemsByUserId(userId);
        return ItemMapper.toItemDto(items);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (!StringUtils.hasLength(text)) {
            return List.of();
        }
        return ItemMapper.toItemDto(itemRepository.searchItem(text));
    }
}
