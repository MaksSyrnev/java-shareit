package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addNewItemRequest(int userId, ItemRequestDto itemRequestDto);

    ItemRequestWithItemsDto getRequestById(int userId, int requestId);

    List<ItemRequestWithItemsDto> getAllUserRequest(int userId);

    List<ItemRequestWithItemsDto> getAllRequest(int userId, int from, int size);

}
