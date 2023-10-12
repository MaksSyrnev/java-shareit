package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exeption.IncorrectItemDataExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemIdExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemOwnerExeption;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
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
    private ItemRepository repository;
    private UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public Item addItem(int userId, ItemDto itemDto) {
        log.info("добавление item, пришло : userId - {}, itemDto- {}", userId, itemDto);
        try {
            User user = userService.getUserById(userId);
            if (!isValidNewItemData(itemDto)) {
                throw new IncorrectItemDataExeption("недостаточно данных");
            }
            Item item = new Item();
            item.setUser(user);
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(Boolean.parseBoolean(itemDto.getAvailable()));
            log.info("добавление item, отправка в сторадж : item - {}", item);
            return repository.save(item);
        } catch (IncorrectUserIdException e) {
            throw new IncorrectItemIdExeption(e.getMessage());
        }
    }

    @Override
    public Item updateItem(int itemId, ItemDto itemDto, int userId) {
        log.info("пачим item, пришло : userId - {}, itemId - {}, itemDto- {}", userId, itemId, itemDto);
        Optional<Item> item = Optional.ofNullable(repository.getById(itemId));
        if (item.isEmpty()) {
            throw new IncorrectItemDataExeption("Вещь с id не найдена");
        }
        if (item.get().getUser().getId() != userId) {
            throw new IncorrectItemOwnerExeption("в доступе отказано, чужая вещь");
        }
        fillItem(item.get(), itemDto);
        log.info("item на выходе получился такой: {}", item.get());
        return item.get();
    }

    @Override
    public Item getItemById(int itemId) {
        Optional<Item> wrapperItem = Optional.ofNullable(repository.getById(itemId));
        if (wrapperItem.isEmpty()) {
            throw new IncorrectItemDataExeption("неверный id вещи");
        }
        return wrapperItem.get();
    }

    @Override
    public List<Item> getAllItemsByUser(int id) {
        User user = userService.getUserById(id);
        final List<Item> itemsAll = repository.findAll();
        final ArrayList<Item> itemsUser = new ArrayList<>();
        for (Item item: itemsAll) {
            if (item.getUser().getId() == id) {
                itemsUser.add(item);
            }
        }
        return itemsUser;
    }

    @Override
    public List<Item> searchItem(String text) {
        ArrayList<Item> itemsResultSearch = new ArrayList<>();
        if (text.isBlank()) {
            return itemsResultSearch;
        }
        String textSearch = text.toLowerCase();
        List<Item> itemsAll = repository.findAll();
        for (Item item: itemsAll) {
            if (((item.getName().toLowerCase().contains(textSearch)) ||
                    (item.getDescription().toLowerCase().contains(textSearch))) && item.isAvailable()) {
                itemsResultSearch.add(item);
            }
        }
        return itemsResultSearch;
    }

    private Boolean isValidNewItemData(ItemDto itemDto) {
        Optional<String> name = Optional.ofNullable(itemDto.getName());
        Optional<String> description = Optional.ofNullable(itemDto.getDescription());
        Optional<String> available = Optional.ofNullable(itemDto.getAvailable());
        if (name.isEmpty() || name.get().isBlank() || description.isEmpty() || description.get().isBlank()
                || available.isEmpty() || available.get().isBlank()) {
            return false;
        }
        return true;
    }

    private void fillItem(Item item, ItemDto itemDto) {
        Optional<String> name = Optional.ofNullable(itemDto.getName());
        if (name.isPresent()) {
            item.setName(name.get());
        }
        Optional<String> description = Optional.ofNullable(itemDto.getDescription());
        if (description.isPresent()) {
            item.setDescription(description.get());
        }
        Optional<String> available = Optional.ofNullable(itemDto.getAvailable());
        if (available.isPresent()) {
            item.setAvailable(Boolean.parseBoolean(available.get()));
        }
        Optional<ItemRequest> request = Optional.ofNullable(itemDto.getRequest());
        if (request.isPresent()) {
            item.setRequest(request.get());
        }
    }
}
