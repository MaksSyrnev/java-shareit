package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.exeption.IncorrectItemDataExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemIdExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemOwnerExeption;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.exeption.IncorrectUserIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toShortBookingDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDtoWithBooking;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository repository, UserService userService, BookingRepository bookingRepository) {
        this.repository = repository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Item addItem(int userId, ItemDto itemDto) {
        log.info("добавление item, пришло : userId - {}, itemDto- {}", userId, itemDto);
        try {
            if (!isValidNewItemData(itemDto)) {
                throw new IncorrectItemDataExeption("недостаточно данных");
            }
            User user = userService.getUserById(userId);
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
        Optional<Item> item = repository.findById(itemId);
        if (item.isEmpty()) {
            throw new IncorrectItemDataExeption("Вещь с id не найдена");
        }
        if (item.get().getUser().getId() != userId) {
            throw new IncorrectItemOwnerExeption("в доступе отказано, чужая вещь");
        }
        fillItem(item.get(), itemDto);
        repository.save(item.get());
        log.info("item на выходе получился такой: {}", item.get());
        return item.get();
    }

    @Override
    public ItemDtoWithBooking getItemById(int userId, int itemId) {
        Optional<Item> wrapperItem = repository.findById(itemId);
        if (wrapperItem.isEmpty()) {
            throw new IncorrectItemIdExeption("неверный id вещи");
        }
        ItemDtoWithBooking itemDto = toItemDtoWithBooking(wrapperItem.get());
        if (wrapperItem.get().getUser().getId() == userId) {
            return fillItemBooking(itemDto);
        }
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getAllItemsByUser(int userId) {
        final List<ItemDtoWithBooking> itemsDtoWithBookings = new ArrayList<>();
        final List<Item> itemsAll = repository.findAllByUserIdOrderByIdAsc(userId);
        if (itemsAll.size() < 1) {
            return itemsDtoWithBookings;
        }
        for (Item item : itemsAll) {
            ItemDtoWithBooking currItemDtoWithBooking = toItemDtoWithBooking(item);
            fillItemBooking(currItemDtoWithBooking);
            itemsDtoWithBookings.add(currItemDtoWithBooking);
        }
        return itemsDtoWithBookings;
    }

    @Override
    public List<Item> searchItem(String text) {
        ArrayList<Item> itemsResultSearch = new ArrayList<>();
        if (text.isBlank()) {
            return itemsResultSearch;
        }
        String textSearch = text.toLowerCase();
        return repository.search(textSearch).stream()
                .filter(i->i.isAvailable())
                .collect(Collectors.toList());
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

    private ItemDtoWithBooking fillItemBooking(ItemDtoWithBooking itemDto) {
        List<Booking> allItemBokings = bookingRepository.findAllByItemIdAndStatusNotOrderByStartDesc(itemDto.getId(),
                BookingDtoState.REJECTED);
        List<Booking> lastBookings = allItemBokings.stream()
                .filter(b->b.getStatus()==BookingDtoState.APPROVED)
                .filter(b->b.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (lastBookings.size() > 0) {
            lastBookings.sort((Booking b1, Booking b2)-> {
                if (b1.getEnd().isAfter(b2.getEnd())) {
                    return 1;
                } else {
                    return -1;
                }
            });
            Booking lastBooking = lastBookings.get(0);
            log.info("Найденный lastBooking - {}", lastBooking);
            itemDto.setLastBooking(toShortBookingDto(lastBooking));
        }
        List<Booking> nextBookings = allItemBokings.stream()
                .filter(b->b.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (nextBookings.size() > 0) {
            nextBookings.sort((Booking b1, Booking b2)-> {
                if (b1.getStart().isBefore(b2.getStart())) {
                    return -1;
                } else {
                    return 1;
                }
            });
            Booking nextBooking = nextBookings.get(0);
            log.info("Найденный futureBooking - {}", nextBooking);
            itemDto.setNextBooking(toShortBookingDto(nextBooking));
        }
        return itemDto;
    }
}
