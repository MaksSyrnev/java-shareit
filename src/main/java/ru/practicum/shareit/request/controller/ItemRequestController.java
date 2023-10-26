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
    public ItemRequest addNewRequest(@RequestHeader ("X-Sharer-User-Id") String headerUserId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto ) {
        int userId = Integer.parseInt(headerUserId);
        return service.addNewItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequest> getAllUserRequest(@RequestHeader ("X-Sharer-User-Id") String headerUserId) {
        int userId = Integer.parseInt(headerUserId);
        return service.getAllUserRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequest> getAllRequest(@RequestHeader ("X-Sharer-User-Id") String headerUserId,
                                              @RequestParam int from, @RequestParam int size) {
        int userId = Integer.parseInt(headerUserId);
        return service.getAllRequest(from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequest getRequestById(@RequestHeader ("X-Sharer-User-Id") String headerUserId,
                                      @PathVariable int requestId) {
        //int userId = Integer.parseInt(headerUserId);
        return service.getRequestById(requestId);
    }
}

/*

четыре новых эндпоинта:

- POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.

- GET /requests — получить список своих запросов вместе с данными об ответах на них. Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате: id вещи, название, её описание description, а также requestId запроса и признак доступности вещи available. Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи. Запросы должны возвращаться в отсортированном порядке от более новых к более старым.

- GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями. С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить. Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично. Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.

- GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.

 */