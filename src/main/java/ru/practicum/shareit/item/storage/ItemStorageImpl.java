package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ItemStorageImpl implements ItemStorage {
    private int id;
    private HashMap<Integer, Item> items;

    public ItemStorageImpl() {
        id = 0;
        this.items = new HashMap<>();
    }

    @Override
    public Item addItem(Item item) {
        id++;
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> updateItem(Item item) {
        if (items.containsKey(item.getId())) {
            Item currentItem = items.get(item.getId());
            currentItem.setName(item.getName());
            currentItem.setDescription(item.getDescription());
            currentItem.setAvailable(item.isAvailable());
            return Optional.of(currentItem);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> getItemById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }
}
