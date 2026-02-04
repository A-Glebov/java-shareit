package ru.practicum.shareit.item.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> itemsByUserId = new HashMap<>();

    @Override
    public Optional<Item> getItemById(long itemId) {
        log.info("Запрос на получение пользователя по id: {}", itemId);
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getItemsByOwner(long userId) {
        log.info("Запрос на получение вещей пользователя id: {}", userId);
        return itemsByUserId.get(userId);
    }

    @Override
    public List<Item> getItemBySearch(String text) {
        log.info("Запрос на получение предметов по ключевому слову {}", text);
        return items.values().stream()
                .filter(item -> item.getAvailable() && ((item.getName().toLowerCase().contains(text.toLowerCase()))
                        || (item.getDescription().toLowerCase().contains(text.toLowerCase()))))
                .collect(Collectors.toList());
    }

    @Override
    public Item saveItem(Item item, long userId) {
        log.info("Запрос на сохранение предмета {} пользователя id: {}", item, userId);
        item.setId(generateId());
        log.debug("Предмету установлен id: {}", item.getId());
        item.setOwnerId(userId);
        log.debug("Предмету присвоен id владельца: {}", item.getOwnerId());
        items.put(item.getId(), item);
        final List<Item> userItems = itemsByUserId.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>());
        userItems.add(item);
        return item;
    }

    private Long generateId() {
        return items.keySet().stream()
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }
}
