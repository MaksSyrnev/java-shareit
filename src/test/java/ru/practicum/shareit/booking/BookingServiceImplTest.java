package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exeptions.IncorrectBookingDataExeption;
import ru.practicum.shareit.booking.exeptions.IncorrectItemIdOrUserIdBoking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Slf4j
public class BookingServiceImplTest {
    UserService mockUserService  = Mockito.mock(UserService.class);
    ItemService mockItemService = Mockito.mock(ItemService.class);
    BookingRepository mockRepository = Mockito.mock(BookingRepository.class);

    final BookingServiceImpl bookingService = new BookingServiceImpl(mockRepository,
            mockItemService, mockUserService);


    @Test
    void testAddNewBookingWithCorrectDataCallSave() {
        BookingDto bookingDtoIncoming = makeBookingDto(1, LocalDateTime.now().plusDays(1),
                (LocalDateTime.now().plusDays(2)));
        User userJon = makeUser(1, "Jon", "jon@dow.com");
        User user = makeUser(2, "Joe", "joe@dow.com");
        ItemDtoWithBooking mockItem = makeItemDtoWithBooking(56, "name", "description", user,
                true, null);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(userJon);

        Mockito
                .when(mockItemService.getItemById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockItem);

        bookingService.addBooking(5, bookingDtoIncoming);

        Mockito.verify(mockRepository, Mockito.times(1))
                .save(Mockito.any());

    }

    @Test
    void testAddNewBookingWithInCorrectDateBooking() {
        BookingDto bookingDtoIncoming = makeBookingDto(1, LocalDateTime.now().plusDays(1),
                (LocalDateTime.now().minusDays(2)));
        User userJon = makeUser(1, "Jon", "jon@dow.com");
        User user = makeUser(2, "Joe", "joe@dow.com");
        ItemDtoWithBooking mockItem = makeItemDtoWithBooking(56, "name", "description", user,
                true, null);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(userJon);

        Mockito
                .when(mockItemService.getItemById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockItem);

        final IncorrectBookingDataExeption ex = assertThrows(
                IncorrectBookingDataExeption.class,
                () -> bookingService.addBooking(5, bookingDtoIncoming)
        );

        assertEquals(ex.getMessage(), "Даты бронирования некорректные",
                "Ошибка при валидации дат отсутсвует");
    }

    @Test
    void testAddNewBookingThroeInvalidOwner() {
        BookingDto bookingDtoIncoming = makeBookingDto(1, LocalDateTime.now().plusDays(1),
                (LocalDateTime.now().plusDays(2)));
        User userJon = makeUser(1, "Jon", "jon@dow.com");
        User user = makeUser(2, "Joe", "joe@dow.com");
        ItemDtoWithBooking mockItem = makeItemDtoWithBooking(56, "name", "description", userJon,
                true, null);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(userJon);

        Mockito
                .when(mockItemService.getItemById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockItem);

        final IncorrectItemIdOrUserIdBoking ex = assertThrows(
                IncorrectItemIdOrUserIdBoking.class,
                () -> bookingService.addBooking(5, bookingDtoIncoming)
        );

        assertEquals(ex.getMessage(), "Пользователь и владелец совпадают по id",
                "Ошибка при валидации владельца вещи отсутствует");
    }

    @Test
    void testAddNewBookingUnAvailableItem() {
        BookingDto bookingDtoIncoming = makeBookingDto(1, LocalDateTime.now().plusDays(1),
                (LocalDateTime.now().plusDays(2)));
        User userJon = makeUser(1, "Jon", "jon@dow.com");
        User user = makeUser(2, "Joe", "joe@dow.com");
        ItemDtoWithBooking mockItem = makeItemDtoWithBooking(56, "name", "description", user,
                false, null);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(userJon);

        Mockito
                .when(mockItemService.getItemById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockItem);

        final IncorrectBookingDataExeption ex = assertThrows(
                IncorrectBookingDataExeption.class,
                () -> bookingService.addBooking(5, bookingDtoIncoming)
        );

        assertEquals(ex.getMessage(), "Вещь недоступна к бронированию",
                "Ошибка при валидации доступности бронирования вещи отсутствует");
    }

    @Test
    void testApproveBookingCallSaveInBase() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Joe", "joe@dow.com");
        Item item = makeItem(56, "name", "description", owner,
                true, null);
        Booking booking = makeBooking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));

        bookingService.approveBooking(2,  1, true);

        Mockito.verify(mockRepository, Mockito.times(1))
                .save(Mockito.any());
    }

    @Test
    void testApproveBookingSetStatus() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Joe", "joe@dow.com");
        Item item = makeItem(56, "name", "description", owner,
                true, null);
        Booking booking = makeBooking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));

        bookingService.approveBooking(2,  1, true);

        Assertions.assertEquals(booking.getStatus(), BookingStatus.APPROVED, "Статус не изменился на апрув");
    }

    @Test
    void testApproveBookingNotOwner() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Joe", "joe@dow.com");
        Item item = makeItem(56, "name", "description", owner,
                true, null);
        Booking booking = makeBooking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));

        final IncorrectItemIdOrUserIdBoking ex = assertThrows(
                IncorrectItemIdOrUserIdBoking.class,
                () -> bookingService.approveBooking(1,  1, true)
        );

        Assertions.assertEquals(ex.getMessage(), "Доступ запрещен", "Ошибка не верная или не произошла");
    }

    @Test
    void testApproveApproovedBooking() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Joe", "joe@dow.com");
        Item item = makeItem(56, "name", "description", owner,
                true, null);
        Booking booking = makeBooking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));

        final IncorrectBookingDataExeption ex = assertThrows(
                IncorrectBookingDataExeption.class,
                () -> bookingService.approveBooking(2,  1, true)
        );

        Assertions.assertEquals(ex.getMessage(), "Бронирование уже подтверждено",
                "Ошибка не верная или не произошла");
    }

    @Test
    void testGetBookingById() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Joe", "joe@dow.com");
        Item item = makeItem(56, "name", "description", owner,
                true, null);
        Booking booking = makeBooking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));

        bookingService.getBookingById(1, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .findById(Mockito.anyInt());
    }

    @Test
    void testGetBookingByIdOtherUser() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Joe", "joe@dow.com");
        Item item = makeItem(56, "name", "description", owner,
                true, null);
        Booking booking = makeBooking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));

        final IncorrectItemIdOrUserIdBoking ex = assertThrows(
                IncorrectItemIdOrUserIdBoking.class,
                () -> bookingService.getBookingById(56, 1)
        );

        Assertions.assertEquals(ex.getMessage(), "Доступ запрещен",
                "Ошибка не верная или не произошла");
    }

    @Test
    void testGetBookingByState() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Joe", "joe@dow.com");
        Item item = makeItem(56, "name", "description", owner,
                true, null);
        Booking booking = makeBooking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);


        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(booker);

        Mockito
                .when(mockRepository.findAllByBookerIdOrderByStartDesc(Mockito.anyInt(), Mockito.any()))
                .thenReturn(null);

        bookingService.getBookingByState(1, "ALL", 0, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .findAllByBookerIdOrderByStartDesc(Mockito.any(), Mockito.any());
    }

    private BookingDto makeBookingDto(int itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);
        return bookingDto;
    }

    private User makeUser(int id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemDtoWithBooking makeItemDtoWithBooking(int id, String name, String description,
                                                      User user, Boolean available, ItemRequest request) {
        ItemDtoWithBooking item = new ItemDtoWithBooking();
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
}
/*
+addBooking
+public Booking approveBooking(int userId, int bookingId, Boolean approved)
+getBookingById

List<Booking> getBookingByState(int userId, String state, int from, int size)
List<Booking> getBookingByOwner(int userId, String state, int from, int size)
 */