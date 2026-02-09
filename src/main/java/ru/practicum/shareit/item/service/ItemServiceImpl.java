package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Запрос на получение предмета по id: {}", itemId);
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id=" + itemId + " не найден"));
        return itemMapper.itemModelToItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(long userId) {
        log.info("Запрос на получение списка предметов пользователя id: {}", userId);
        userService.getUserById(userId);
        return itemRepository.getItemsByOwner(userId).stream()
                .map(itemMapper::itemModelToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        log.info("Запрос на получение списка предметов по ключевому слову: {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.getItemBySearch(text).stream()
                .map(itemMapper::itemModelToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto saveItem(ItemDto itemDto, long userId) {
        log.info("Запрос на добавление предмета: {} пользователя id: {}", itemDto, userId);
        UserDto ownerDto = userService.getUserById(userId);
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус вещи должен быть указан");
        }
        if (itemDto.getName().isBlank()) {
            throw new ValidationException("Не указано название предмета");
        }
        if (itemDto.getDescription().isBlank()) {
            throw new ValidationException("Добавьте описание предмета");
        }
        Item item = itemRepository.saveItem(itemMapper.itemDtoToItemModel(itemDto), userMapper.userDtoToUserModel(ownerDto));
        return itemMapper.itemModelToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long userId) {
        log.info("Запрос на обновление данных предмета id: {} владельца id: {}", itemId, userId);
        userService.getUserById(userId);
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id=" + itemId + " не найден"));
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
        if (item.getOwner().getId() == userId) {
            if (name != null && !name.isBlank()) {
                item.setName(name);
                log.debug("Предмету установлено имя: {}", name);
            }
            if (description != null && !description.isBlank()) {
                item.setDescription(description);
                log.debug("Предмету установлено описание: {}", description);
            }
            if (available != null) {
                item.setAvailable(available);
                log.debug("Предмету установлен статус занятости: {}", available);
            }
        } else {
            throw new NotFoundException("Пользователь id=" + itemId + " не является собственником " + name);
        }
        return itemMapper.itemModelToItemDto(item);
    }
}
