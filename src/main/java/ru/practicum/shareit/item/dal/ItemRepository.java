package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getItemById(long itemId);

    List<Item> getItemsByOwner(long userId);

    List<Item> getItemBySearch(String text);

    Item saveItem(Item item, User owner);

}
