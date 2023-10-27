package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        Optional<ItemRequest> request = Optional.ofNullable(item.getRequest());
        if (request.isPresent()) {
            itemDto.setRequestId(request.get().getId());
        }
        return itemDto;
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item) {
        ItemDtoWithBooking itemDto = new ItemDtoWithBooking();
        itemDto.setId(item.getId());
        itemDto.setUser(item.getUser());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        return itemDto;
    }

}
