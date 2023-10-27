package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ShortCommentDto;
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
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") String headerUserId, @Valid @RequestBody ItemDto itemDto) {
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
    public List<ItemDtoWithBooking> getItems(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "20") int size) {
        log.info("+GET /items, параметры from - {}, size - {}", from, size);
        int userId = Integer.parseInt(headerUserId);
        return service.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                          @PathVariable int itemId) {
        int userId = Integer.parseInt(headerUserId);
        return service.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "20") int size) {
        return service.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ShortCommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                            @PathVariable int itemId, @RequestBody CommentDto commentDto) {
        int userId = Integer.parseInt(headerUserId);
        return service.addCommentToItem(userId, itemId, commentDto);
    }
}