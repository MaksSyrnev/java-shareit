package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.ConstantGatway.HEADER_USER_NAME;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HEADER_USER_NAME) long userId,
                                          @RequestBody @Validated({Marker.OnCreate.class}) ItemDto itemDto) {
        log.info("created item {}, userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER_USER_NAME) long userId,
                           @PathVariable long itemId,
                           @RequestBody @Validated({Marker.OnUpdate.class}) ItemDto itemDto) {
        log.info("Update item {}, itemId={}, userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(HEADER_USER_NAME) long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(HEADER_USER_NAME) long userId,
                                          @PathVariable long itemId) {
        log.info("Get item - {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(HEADER_USER_NAME) long userId,
            @RequestParam(name = "text") String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("search {}, from={}, size={}", text, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader(HEADER_USER_NAME) long userId,
                                            @PathVariable long itemId, @RequestBody @Valid CommentDto commentDto) {
        log.info("Add comment - {} to item - {}, userId={}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}