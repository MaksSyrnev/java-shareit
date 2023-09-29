package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.Optional;

public class ItemServiceImpl implements ItemService{
    private ItemStorage storage;

    @Autowired
    public ItemServiceImpl(ItemStorage storage) {
        this.storage = storage;
    }

    @Override
    public Item addItem(int userId, ItemDto itemDto) {
        //to do
        //проверить id пользователя
        // создать объект сущность вещь
        // насытить сущность данными
        // передать в метод стораджа
        // обработать ответ
        return null;
    }

    @Override
    public Item updateItem(int itemId, ItemDto itemDto, int userId) {
        //to do
        //проверить id пользователя
        // создать объект сущность вещь
        // насытить сущность данными
        // передать в метод стораджа
        // обработать ответ
        return null;
    }

    @Override
    public Item getItemById(int itemId) {
        Optional<Item> item = storage.getItemById(itemId);
        if (item.isEmpty()) {
            throw new RuntimeException();
        }
        return item.get();
    }

    @Override
    public List<Item> getAllItemsByUser(int id) {
        //to do
        //проверить id пользователя и получить его
        // получить список всех вещей из стороджа
        // отфильтровать нужные
        // обработать ответ
        return null;
    }

    @Override
    public List<Item> searchItem(String text) {
        //to do
        // получить список всех вещей из стороджа
        // отфильтровать нужные по названию
        // отфильтровать нужные по описанию
        // может фильтровать их одно функцией по обоим полям сразу
        // вернуть ответ
        return null;
    }
}
