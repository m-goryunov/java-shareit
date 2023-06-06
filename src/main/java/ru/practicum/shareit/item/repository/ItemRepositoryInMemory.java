package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ItemRepositoryInMemory {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    
    public Item createItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        log.info("Вещь создана.{}", item.getId());
        return item;
    }

    
    public void updateItemById(Item item) {
        items.put(item.getId(), item);
        log.info("Вещь обновлена {}", item.getId());
    }

    
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    
    public List<Item> getAllItemsByUserId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    
    public List<Item> searchItem(String text) {
        log.info("Запуск поиска по тексту: {}", text);
        return items.values()
                .stream()
                .filter(item ->
                        ((item.getDescription().toLowerCase().contains(text.toLowerCase())
                                || item.getName().toLowerCase().contains(text.toLowerCase())
                        )
                                && item.getAvailable().equals(true)))
                .collect(Collectors.toList());
    }
}
