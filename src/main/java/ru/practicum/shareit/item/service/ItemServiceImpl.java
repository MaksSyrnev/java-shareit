package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exeption.IncorrectItemDataExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemIdExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemOwnerExeption;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.exeption.IncorrectUserIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private ItemStorage storage;
    private UserService userService;

    @Autowired
    public ItemServiceImpl(ItemStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    @Override
    public Item addItem(int userId, ItemDto itemDto) {
        log.info("добавление item, пришло : userId - {}, itemDto- {}", userId, itemDto);
        try {
            User user = userService.getUserById(userId);
            Item item = new Item();
            Optional<String> name = Optional.ofNullable(itemDto.getName());
            Optional<String> description = Optional.ofNullable(itemDto.getDescription());
            Optional<String> available = Optional.ofNullable(itemDto.getAvailable());
            if (name.isEmpty() || name.get().isBlank() || description.isEmpty() || description.get().isBlank()
                    || available.isEmpty() || available.get().isBlank()) {
                throw new IncorrectItemDataExeption("недостаточно данных");
            }
            item.setUser(user);
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(Boolean.parseBoolean(available.get()));
            log.info("добавление item, отправка в сторадж : item - {}", item);
            return storage.addItem(item);
        } catch (IncorrectUserIdException e) {
            throw new IncorrectItemIdExeption(e.getMessage());
        }
    }

    @Override
    public Item updateItem(int itemId, ItemDto itemDto, int userId) {
        log.info("пачим item, пришло : userId - {}, itemId - {}, itemDto- {}", userId, itemId, itemDto);
        Optional<Item> item = storage.getItemById(itemId);
        if (item.isEmpty()) {
            throw new IncorrectItemDataExeption("Вещь с id не найдена");
        }
        if (item.get().getUser().getId() != userId) {
            throw new IncorrectItemOwnerExeption("в доступе отказано, чужая вещь");
        }
        Optional<String> name = Optional.ofNullable(itemDto.getName());
        if (name.isPresent()) {
           item.get().setName(name.get());
        }
        Optional<String> description = Optional.ofNullable(itemDto.getDescription());
        if (description.isPresent()) {
            item.get().setDescription(description.get());
        }
        Optional<String> available = Optional.ofNullable(itemDto.getAvailable());
        if (available.isPresent()) {
            item.get().setAvailable(Boolean.parseBoolean(available.get()));
        }
        Optional<ItemRequest> request = Optional.ofNullable(itemDto.getRequest());
        if (request.isPresent()) {
            item.get().setRequest(request.get());
        }
        log.info("item на выходе получился такой: {}", item.get());
        return item.get();
    }

    @Override
    public Item getItemById(int itemId) {
        Optional<Item> item = storage.getItemById(itemId);
        if (item.isEmpty()) {
            throw new IncorrectItemDataExeption("неверный id вещи");
        }
        return item.get();
    }

    @Override
    public List<Item> getAllItemsByUser(int id) {
        User user = userService.getUserById(id);
        List<Item> itemsAll = storage.getAllItems();
        ArrayList<Item> itemsUser = new ArrayList<>();
        for (Item i: itemsAll) {
            if (i.getUser().getId() == id) {
                itemsUser.add(i);
            }
        }
        return itemsUser;
    }

    @Override
    public List<Item> searchItem(String text) {
        ArrayList<Item> itemsForSearch = new ArrayList<>();
        if (text.isBlank()) {
            return itemsForSearch;
        }
        String textForSearch = text.toLowerCase();
        List<Item> itemsAll = storage.getAllItems();
        for (Item i: itemsAll) {
            if (((i.getName().toLowerCase().contains(textForSearch)) ||
                    (i.getDescription().toLowerCase().contains(textForSearch))) && i.isAvailable()) {
                itemsForSearch.add(i);
            }
        }
        return itemsForSearch;
    }
}
