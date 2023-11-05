package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public static ItemRequestWithItemsDto makeRequestWithItemsDto(ItemRequest request, List<ItemDto> items) {
        ItemRequestWithItemsDto reqWithItems = new ItemRequestWithItemsDto();
        reqWithItems.setId(request.getId());
        reqWithItems.setCreated(request.getCreated());
        reqWithItems.setDescription(request.getDescription());
        reqWithItems.setItems(items);
        return reqWithItems;
    }
}
