package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequest addNewRequest(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        int userId = headerUserId;
        return service.addNewItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getAllUserRequest(@RequestHeader("X-Sharer-User-Id") int headerUserId) {
        int userId = headerUserId;
        return service.getAllUserRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getAllRequest(@RequestHeader ("X-Sharer-User-Id") String headerUserId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "20") int size) {
        log.info("+GET /requests/all, параметры from - {}, size - {}", from, size);
        int userId = Integer.parseInt(headerUserId);
        return service.getAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestById(@RequestHeader ("X-Sharer-User-Id") String headerUserId,
                                      @PathVariable int requestId) {
        int userId = Integer.parseInt(headerUserId);
        return service.getRequestById(userId, requestId);
    }
}
