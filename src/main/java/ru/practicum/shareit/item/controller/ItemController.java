package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info("Запрос на получение предмета по id = {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение списка вещей пользователя id = {}", userId);
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        log.info("Запрос на поиск вещей по ключевому слову: {}", text);
        return itemService.getItemsBySearch(text);
    }

    @PostMapping
    public ItemDto saveItem(@RequestBody ItemDto itemDto,
                            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на сохранение предмета {} пользователя id = {}", itemDto, userId);
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на обновление данных предмета {} пользователя id: {}", itemDto, userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }


}
