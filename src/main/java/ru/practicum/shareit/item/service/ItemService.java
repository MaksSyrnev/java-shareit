package ru.practicum.shareit.item.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(int userId, ItemDto itemDto);

    Item updateItem(int itemId, ItemDto itemDto, int userId);

    List<Item> getAllItemsByUser(int id);

    Item getItemById(int itemId);

    List<Item> searchItem(@RequestParam String text);
}
