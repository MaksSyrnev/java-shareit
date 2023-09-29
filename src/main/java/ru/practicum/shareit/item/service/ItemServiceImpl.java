package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemServiceImpl implements ItemService{
    @Override
    public Item addItem(int userId, ItemDto itemDto) {
        return null;
    }

    @Override
    public Item updateItem(int itemId, ItemDto itemDto, int userId) {
        return null;
    }

    @Override
    public List<Item> getAllItemsByUser(int id) {
        return null;
    }

    @Override
    public Item getItemById(int itemId) {
        return null;
    }

    @Override
    public List<Item> searchItem(String text) {
        return null;
    }
}
