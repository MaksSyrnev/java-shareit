package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exeption.IncorrectDataCommentExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemDataExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemIdExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemOwnerExeption;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentReopository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestReopository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.domain.PageRequest.of;

public class ItemServiceImplTest {

    private final ItemRepository mockRepository = Mockito.mock(ItemRepository.class);
    private final UserService mockUserService = Mockito.mock(UserService.class);
    private final BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    private final CommentReopository mockCommentsReopository = Mockito.mock(CommentReopository.class);
    private final ItemRequestReopository mockRequestReopository = Mockito.mock(ItemRequestReopository.class);

    final ItemServiceImpl itemService = new ItemServiceImpl(mockRepository, mockUserService,
            mockBookingRepository, mockCommentsReopository, mockRequestReopository);

    @Test
    @DisplayName("AddItem - Вызов метода сохранения реопозитория")
    void testAddItem() {
        ItemDto itemDto = makeItemDto("Вещь", "Обалденная", true);
        User owner = makeUser(1, "Jon", "jon@dow.com");
        Item savedItem = makeItem(1, "Вещь", "Обалденная", owner, true, null);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(owner);

        Mockito
                .when(mockRepository.save(Mockito.any()))
                .thenReturn(savedItem);

        itemService.addItem(1, itemDto);

        Mockito.verify(mockRepository, Mockito.times(1))
                .save(Mockito.any());
    }

    @Test
    @DisplayName("AddItem - некорректные данные для создания Item")
    void testAddItemWithIncorrectData() {
        ItemDto itemDto = makeItemDto(" ", "Обалденная", true);
        User owner = makeUser(1, "Jon", "jon@dow.com");

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(owner);

        final IncorrectItemDataExeption ex = assertThrows(
                IncorrectItemDataExeption.class,
                () -> itemService.addItem(1, itemDto)
        );

        Assertions.assertEquals(ex.getMessage(), "недостаточно данных",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("UpdateItem - вызов методов реопозитория поиска и сохранения")
    void testUpdateItem() {
        User owner = makeUser(1, "Jon", "jon@dow.com");
        Item item = makeItem(1, "Вещь", "Обалденная", owner, true, null);
        ItemDto itemDto = makeItemDto("Вещица ", "Обалденная", false);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        itemService.updateItem(1, itemDto, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .save(Mockito.any());

        Mockito.verify(mockRepository, Mockito.times(1))
                .findById(Mockito.any());

    }

    @Test
    @DisplayName("UpdateItem - id вещи не существует")
    void testUpdateItemNotFound() {
        ItemDto itemDto = makeItemDto("Вещица ", "Обалденная", false);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final IncorrectItemDataExeption ex = assertThrows(
                IncorrectItemDataExeption.class,
                () -> itemService.updateItem(1, itemDto, 1)
        );

        Assertions.assertEquals(ex.getMessage(), "Вещь с id не найдена",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("UpdateItem - обновление информации не собственником")
    void testUpdateItemNotOwner() {
        User owner = makeUser(1, "Jon", "jon@dow.com");
        Item item = makeItem(1, "Вещь", "Обалденная", owner, true, null);
        ItemDto itemDto = makeItemDto("Вещица ", "Обалденная", false);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        final IncorrectItemOwnerExeption ex = assertThrows(
                IncorrectItemOwnerExeption.class,
                () -> itemService.updateItem(1, itemDto, 2)
        );

        Assertions.assertEquals(ex.getMessage(), "в доступе отказано, чужая вещь",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("GetItemById - не владелец вещи, обращения к реопозиториям Item и Comment, к букингу нет")
    void testGetItemByIdNotOwner() {
        User owner = makeUser(1, "Jon", "jon@dow.com");
        User bill = makeUser(2, "Bill", "bill@dow.com");
        Item item = makeItem(1, "Вещь", "Обалденная", owner, true, null);
        ArrayList<Comment> comments = new ArrayList<>();
        Comment commentOne = makeComent(1, "text", item, bill, LocalDateTime.now());
        comments.add(commentOne);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockCommentsReopository.findAllByItemId(Mockito.anyInt()))
                .thenReturn(comments);

        itemService.getItemById(2, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .findById(Mockito.any());

        Mockito.verify(mockCommentsReopository, Mockito.times(1))
                .findAllByItemId(Mockito.anyInt());

        Mockito.verifyNoInteractions(mockBookingRepository);
    }

    @Test
    @DisplayName("GetItemById - вещь не найдена")
    void testGetItemByIdNotFound() {
        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final IncorrectItemIdExeption ex = assertThrows(
                IncorrectItemIdExeption.class,
                () -> itemService.getItemById(2, 1)
        );

        Assertions.assertEquals(ex.getMessage(), "неверный id вещи",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("GetItemById - просмотр вещи собственником, обращение к букингу")
    void testGetItemById() {
        User owner = makeUser(1, "Jon", "jon@dow.com");
        User booker = makeUser(2, "Jony", "jony@dow.com");
        Item item = makeItem(1, "Вещь", "Обалденная", owner, true, null);
        ArrayList<Comment> comments = new ArrayList<>();
        Booking lastBooking = makeBooking(1, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2), item, booker, BookingStatus.APPROVED);
        Booking nextBooking = makeBooking(3, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3), item, booker, BookingStatus.APPROVED);
        Booking middleBooking = makeBooking(13, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);
        List<Booking> bookings = List.of(lastBooking, nextBooking, middleBooking);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockCommentsReopository.findAllByItemId(Mockito.anyInt()))
                .thenReturn(comments);

        Mockito
                .when(mockBookingRepository.findAllByItemIdOrderByStartDesc(Mockito.anyInt()))
                .thenReturn(bookings);

        itemService.getItemById(1, 1);

        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByItemIdOrderByStartDesc(Mockito.anyInt());
    }

    @Test
    @DisplayName("GetAllItemsByUser - просмотр всех вещей собственником, обращение к нужному методу реопозитория")
    void testGetAllItemsByUser() {
        User user = makeUser(1, "Jon", "jon@dow.com");
        PageRequest page = of(0, 10);
        List<Item> items = new ArrayList<>();
        Page<Item> pagedResponse = new PageImpl(items);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(user);

        Mockito
                .when(mockRepository.findAllByUserIdOrderByIdAsc(1, page))
                .thenReturn(pagedResponse);

        itemService.getAllItemsByUser(1, 0, 10);

        Mockito.verify(mockRepository, Mockito.times(1))
                .findAllByUserIdOrderByIdAsc(1, page);
    }

    @Test
    @DisplayName("GetAllItemsByUser - неверный параметр пагинации")
    void testGetAllItemsByUserIncorrectPageNumber() {
        final IncorrectItemDataExeption ex = assertThrows(
                IncorrectItemDataExeption.class,
                () -> itemService.getAllItemsByUser(11, 0, -10)
        );

        Assertions.assertEquals(ex.getMessage(), "некорректное значение параметров пагинации",
                "Ошибка не верная или не произошла");
    }


    @Test
    @DisplayName("SearchItem - вызов правильного метода реопозитория")
    void testSearchItem() {
        PageRequest page = of(0, 10);
        List<Item> items = new ArrayList<>();
        Page<Item> pagedResponse = new PageImpl(items);
        Mockito
                .when(mockRepository.search("текст", page))
                .thenReturn(pagedResponse);

        itemService.searchItem("текст", 0, 10);

        Mockito.verify(mockRepository, Mockito.times(1))
                .search("текст", page);
    }

    @Test
    @DisplayName("SearchItem - вызов при пустом поиске, обращения к реопозиторию нет")
    void testSearchItemWithBlanktext() {
        itemService.searchItem(" ", 0, 10);

        Mockito.verifyNoInteractions(mockRepository);
    }

    @Test
    @DisplayName("AddCommentToItem - метод save к реопозиторию комментариев")
    void testAddCommentToItem() {
        User user = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Jony", "jony@dow.com");
        Item item = makeItem(1, "Вещь", "Обалденная", owner, true, null);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        Booking booking = makeBooking(1, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(3),
                item, user,  BookingStatus.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(user);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockBookingRepository.findAllByItemIdAndBookerIdAndStatus(Mockito.anyInt(),
                        Mockito.anyInt(), Mockito.any()))
                .thenReturn(bookings);

        itemService.addCommentToItem(1,1, commentDto);

        Mockito.verify(mockCommentsReopository, Mockito.times(1))
                .save(Mockito.any());
    }

    @Test
    @DisplayName("AddCommentToItem - пустой комментарий")
    void testAddBlankCommentToItem() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(" ");

        final IncorrectDataCommentExeption ex = assertThrows(
                IncorrectDataCommentExeption.class,
                () -> itemService.addCommentToItem(1,1, commentDto)
        );

        Assertions.assertEquals(ex.getMessage(), "пустой комментарий",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("AddCommentToItem - владелец пишет комментариев")
    void testAddCommentToItemOwner() {
        User owner = makeUser(2, "Jony", "jony@dow.com");
        Item item = makeItem(1, "Вещь", "Обалденная", owner, true, null);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(owner);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        final IncorrectItemDataExeption ex = assertThrows(
                IncorrectItemDataExeption.class,
                () -> itemService.addCommentToItem(2,1, commentDto)
        );

        Assertions.assertEquals(ex.getMessage(),
                "неверный id пользователя, владелец не может добавлять комментарий",
                "Ошибка не верная или не произошла");
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private User makeUser(int id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item makeItem(int id, String name, String description, User user, Boolean available,
                          ItemRequest request) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setUser(user);
        item.setDescription(description);
        item.setAvailable(available);
        item.setRequest(request);
        return item;
    }

    private Booking makeBooking(int id, LocalDateTime start, LocalDateTime end, Item item, User booker,
                                BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }

    private Comment makeComent(int id, String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);
        return comment;
    }
}
