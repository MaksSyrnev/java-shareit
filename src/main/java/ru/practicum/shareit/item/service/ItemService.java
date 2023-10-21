package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(int userId, ItemDto itemDto);

    Item updateItem(int itemId, ItemDto itemDto, int userId);

    List<ItemDtoWithBooking> getAllItemsByUser(int id);

    ItemDtoWithBooking getItemById(int userId, int itemId);

    List<Item> searchItem(String text);
}
