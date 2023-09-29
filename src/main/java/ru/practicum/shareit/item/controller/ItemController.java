package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @Autowired
    public ItemController(ItemService itemService) {
        this.service = itemService;
    }

    @PostMapping
    public Item addItem(HttpServletRequest request, ItemDto itemDto) {
        int userId = Integer.parseInt(request.getHeader("X-Sharer-User-Id"));
        return service.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(HttpServletRequest request, @PathVariable int itemId, ItemDto itemDto) {
        int userId = Integer.parseInt(request.getHeader("X-Sharer-User-Id"));
        return service.updateItem(itemId, itemDto, userId);
    }

    @GetMapping
    public List<Item> getItems(HttpServletRequest request) {
        int userId = Integer.parseInt(request.getHeader("X-Sharer-User-Id"));
        return service.getAllItemsByUser(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable int itemId) {
        return service.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text) {
        return service.searchItem(text);
    }
}