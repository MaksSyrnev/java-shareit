package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(String.valueOf(item.isAvailable()));
        itemDto.setRequest(item.getRequest());
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
