package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addNewItemRequest(int userId, ItemRequestDto itemRequestDto);

    ItemRequest getRequestById(int requestId);

    List<ItemRequest> getAllUserRequest(int userId);

    List<ItemRequest> getAllRequest(int from, int size);

}
