package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ShortCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public Item addItem(@RequestHeader("X-Sharer-User-Id") String headerUserId, @Valid @RequestBody ItemDto itemDto) {
        int userId = Integer.parseInt(headerUserId);
        return service.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") String headerUserId, @PathVariable int itemId,
                           @Valid @RequestBody ItemDto itemDto) {
        int userId = Integer.parseInt(headerUserId);
        return service.updateItem(itemId, itemDto, userId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getItems(@RequestHeader("X-Sharer-User-Id") String headerUserId) {
        int userId = Integer.parseInt(headerUserId);
        return service.getAllItemsByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                          @PathVariable int itemId) {
        int userId = Integer.parseInt(headerUserId);
        return service.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text) {
        return service.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public ShortCommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                            @PathVariable int itemId, @RequestBody CommentDto commentDto ) {
        int userId = Integer.parseInt(headerUserId);
        return service.addCommentToItem(userId, itemId, commentDto);
    }
}