package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByOwner(long userId);

    List<ItemDto> getItemsBySearch(String text);

    ItemDto saveItem(ItemDto item, long userId);

    ItemDto updateItem(long itemId, ItemDto itemDto, long userId);
}
