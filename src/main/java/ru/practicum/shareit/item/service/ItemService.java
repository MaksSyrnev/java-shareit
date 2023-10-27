package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ShortCommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(int userId, ItemDto itemDto);

    Item updateItem(int itemId, ItemDto itemDto, int userId);

    List<ItemDtoWithBooking> getAllItemsByUser(int id, int from, int size);

    ItemDtoWithBooking getItemById(int userId, int itemId);

    List<Item> searchItem(String text, int from, int size);

    ShortCommentDto addCommentToItem(int userId, int itemId, CommentDto commentDto);

}
