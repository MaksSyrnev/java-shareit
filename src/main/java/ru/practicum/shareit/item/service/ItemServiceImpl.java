package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ShortCommentDto;
import ru.practicum.shareit.item.exeption.IncorrectDataCommentExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemDataExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemIdExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemOwnerExeption;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentReopository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestReopository;
import ru.practicum.shareit.user.exeption.IncorrectUserIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.PageRequest.of;
import static ru.practicum.shareit.booking.dto.BookingMapper.toShortBookingDto;
import static ru.practicum.shareit.item.dto.CommentMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentMapper.toShortCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDtoWithBooking;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentReopository commentsReopository;
    private final ItemRequestReopository requestReopository;

    @Autowired
    public ItemServiceImpl(ItemRepository repository, UserService userService,
                           BookingRepository bookingRepository, CommentReopository commentsReopository,
                           ItemRequestReopository requestReopository) {
        this.repository = repository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentsReopository = commentsReopository;
        this.requestReopository = requestReopository;
    }

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
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
            item.setAvailable(itemDto.getAvailable());
            Optional<Integer> requestId = Optional.ofNullable(itemDto.getRequestId());
            if (requestId.isPresent() && (requestId.get() != 0)) {
                ItemRequest request = requestReopository.findById(requestId.get()).orElseThrow(
                        () -> new IncorrectItemIdExeption("неверный id запроса"));
                item.setRequest(request);
            }
            log.info("добавление item, отправка в сторадж : item - {}", item);
            Item finalItem = repository.save(item);
            return toItemDto(finalItem);
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
            fillItemBooking(itemDto);
        }
        List<Comment> comments = commentsReopository.findAllByItemId(itemId);
        if (!comments.isEmpty()) {
            List<ShortCommentDto> shortComments = comments.stream()
                            .map(coment -> toShortCommentDto(coment))
                            .collect(Collectors.toList());
            itemDto.setComments(shortComments);
        }
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getAllItemsByUser(int userId, int from, int size) {
        if ((from < 0) || (size < 0)) {
            throw new IncorrectItemDataExeption("некорректное значение парметров пагинации");
        }
        User user = userService.getUserById(userId);
        PageRequest page = of(from > 0 ? from / size : 0, size);
        final List<ItemDtoWithBooking> itemsDtoWithBookings = new ArrayList<>();
        final List<Item> itemsAll = repository.findAllByUserIdOrderByIdAsc(userId, page).getContent();
        if (itemsAll.isEmpty()) {
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
    public List<Item> searchItem(String text, int from, int size) {
        if ((from < 0) || (size < 0)) {
            throw new IncorrectItemDataExeption("некорректное значение парметров пагинации");
        }
        PageRequest page = of(from > 0 ? from / size : 0, size);
        ArrayList<Item> itemsResultSearch = new ArrayList<>();
        if (text.isBlank()) {
            return itemsResultSearch;
        }
        String textSearch = text.toLowerCase();
        return repository.search(textSearch, page).getContent().stream()
                .filter(item -> item.isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public ShortCommentDto addCommentToItem(int userId, int itemId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new IncorrectDataCommentExeption("пустой комментарий");
        }
        User user = userService.getUserById(userId);
        Optional<Item> item = repository.findById(itemId);
        if (item.isEmpty()) {
            throw new IncorrectItemIdExeption("неверный id вещи");
        }
        if (item.get().getUser().getId() == userId) {
            throw new IncorrectItemDataExeption("неверный id пользователя, владелеу не может добавлять комментарий");
        }
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatus(itemId, userId,
                BookingStatus.APPROVED)
                .stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new IncorrectItemDataExeption("неверный id пользователя, он еще не брал вещь");
        }
        Comment comment = toComment(commentDto);
        comment.setItem(item.get());
        comment.setAuthor(user);
        commentsReopository.save(comment);
        return toShortCommentDto(comment);
    }

    private Boolean isValidNewItemData(ItemDto itemDto) {
        Optional<String> name = Optional.ofNullable(itemDto.getName());
        Optional<String> description = Optional.ofNullable(itemDto.getDescription());
        Optional<Boolean> available = Optional.ofNullable(itemDto.getAvailable());
        if (name.isEmpty() || name.get().isBlank() || description.isEmpty() || description.get().isBlank()
                || available.isEmpty()) {
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
        Optional<Boolean> available = Optional.ofNullable(itemDto.getAvailable());
        if (available.isPresent()) {
            item.setAvailable(available.get());
        }
        Optional<Integer> requestId = Optional.ofNullable(itemDto.getRequestId());
        if (requestId.isPresent() && (requestId.get() != 0)) {
            ItemRequest request = requestReopository.findById(requestId.get()).orElseThrow(
                    () -> new IncorrectItemIdExeption("неверный id запроса"));
            item.setRequest(request);
        }
    }

    private ItemDtoWithBooking fillItemBooking(ItemDtoWithBooking itemDto) {
        final List<Booking> allItemBokings = bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId());
        final List<Booking> lastBookings = allItemBokings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (!lastBookings.isEmpty()) {
            lastBookings.sort((Booking booking1, Booking booking2) -> {
                if (booking1.getEnd().isAfter(booking2.getEnd())) {
                    return -1;
                } else {
                    return 1;
                }
            });
            Booking lastBooking = lastBookings.get(0);
            log.info("Найденный lastBooking - {}", lastBooking);
            itemDto.setLastBooking(toShortBookingDto(lastBooking));
        }
        final List<Booking> nextBookings = allItemBokings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (!nextBookings.isEmpty()) {
            nextBookings.sort((Booking booking1, Booking booking2) -> {
                if (booking1.getStart().isBefore(booking2.getStart())) {
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
