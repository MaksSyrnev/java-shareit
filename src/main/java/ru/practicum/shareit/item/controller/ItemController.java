package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @Autowired
    public ItemController(ItemService itemService) {
        this.service = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") int headerUserId, @Valid @RequestBody ItemDto itemDto) {
        return service.addItem(headerUserId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") int headerUserId, @PathVariable int itemId,
                           @Valid @RequestBody ItemDto itemDto) {
        return service.updateItem(itemId, itemDto, headerUserId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getItems(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("+GET /items, параметры from - {}, size - {}", from, size);
        return service.getAllItemsByUser(headerUserId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                                          @PathVariable int itemId) {
        return service.getItemById(headerUserId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text,
                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                 @RequestParam(defaultValue = "20") @Positive int size) {
        return service.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ShortCommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                                            @PathVariable int itemId, @RequestBody CommentDto commentDto) {
        return service.addCommentToItem(headerUserId, itemId, commentDto);
    }
}