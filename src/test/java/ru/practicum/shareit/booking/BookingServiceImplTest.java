package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exeptions.IncorrectBookingDataExeption;
import ru.practicum.shareit.booking.exeptions.IncorrectItemIdOrUserIdBoking;
import ru.practicum.shareit.booking.exeptions.IncorrectStatusBookingExeption;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.exeption.IncorrectDataItemRequestExeption;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.domain.PageRequest.of;

@Slf4j
public class BookingServiceImplTest {
    private final UserService mockUserService  = Mockito.mock(UserService.class);
    private final ItemService mockItemService = Mockito.mock(ItemService.class);
    private final BookingRepository mockRepository = Mockito.mock(BookingRepository.class);

    final BookingServiceImpl bookingService = new BookingServiceImpl(mockRepository,
            mockItemService, mockUserService);


    @Test
    @DisplayName("AddNewBooking - есть ли вызов сохранения в реопозиторий")
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
    @DisplayName("AddNewBooking - с некорректными датами бронирования")
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
    @DisplayName("AddNewBooking - когда букер и владелец совпадают")
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
    @DisplayName("AddNewBooking - вещь недоступна к бронированию")
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
    @DisplayName("ApproveBooking - Вызов метода сохранения реопозитория")
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
    @DisplayName("ApproveBooking - измененее статуса на Одобрено ")
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
    @DisplayName("ApproveBooking - измененее статуса на Отклонен ")
    void testApproveBookingSetStatusReject() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        User owner = makeUser(2, "Joe", "joe@dow.com");
        Item item = makeItem(56, "name", "description", owner,
                true, null);
        Booking booking = makeBooking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));

        bookingService.approveBooking(2,  1, false);

        Assertions.assertEquals(booking.getStatus(), BookingStatus.REJECTED, "Статус не изменился на отклонен");
    }

    @Test
    @DisplayName("ApproveBooking - не владелец вещи")
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
    @DisplayName("ApproveBooking - букинг уже одобрен, повторное одобрение")
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
    @DisplayName("ApproveBooking - букинг не найден")
    void testApproveBookingNotFound() {
        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final IncorrectItemIdOrUserIdBoking ex = assertThrows(
                IncorrectItemIdOrUserIdBoking.class,
                () -> bookingService.approveBooking(1,  1, true)
        );

        Assertions.assertEquals(ex.getMessage(), "Букинг с таким id не найден", "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("GetBookingById - Вызов метода реопозитория")
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
    @DisplayName("GetBookingById - запрашивает не букер и не владелец вещи")
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
    @DisplayName("GetBookingByState - Вызов корректного метода реопозитория")
    void testGetBookingByStateCallReopository() {
        User booker = makeUser(1, "Jon", "jon@dow.com");
        PageRequest page = of(0, 10);
        List<Booking> bookings = new ArrayList<>();
        Page<Booking> pagedResponse = new PageImpl(bookings);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(booker);

        Mockito
                .when(mockRepository.findAllByBookerIdOrderByStartDesc(1, page))
                .thenReturn(pagedResponse);

        bookingService.getBookingByState(1, "ALL", 0, 10);

        Mockito.verify(mockRepository, Mockito.times(1))
                .findAllByBookerIdOrderByStartDesc(1, page);
    }

    @Test
    @DisplayName("GetBookingByState - передан несуществующий статус")
    void testGetBookingByStateIncorrectState() {
        User booker = makeUser(1, "Jon", "jon@dow.com");

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(booker);

        final IncorrectStatusBookingExeption ex = assertThrows(
                IncorrectStatusBookingExeption.class,
                () -> bookingService.getBookingByState(1, "LOVE", 0, 10)
        );

        Assertions.assertEquals(ex.getMessage(), "LOVE",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("GetBookingByState - некорректные значения для пагинации")
    void testGetBookingByStateIncorrectPageNumber() {
        final IncorrectDataItemRequestExeption ex = assertThrows(
                IncorrectDataItemRequestExeption.class,
                () -> bookingService.getBookingByState(1, "ALL", 0, -10)
        );

        Assertions.assertEquals(ex.getMessage(), "некорректное значение параметров пагинации",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("GetBookingByOwner - Вызов корректного метода реопозитория")
    void testGetBookingByOwnerCallReopository() {
        User owner = makeUser(1, "Jon", "jon@dow.com");
        PageRequest page = of(0, 10);
        List<Booking> bookings = new ArrayList<>();
        Page<Booking> pagedResponse = new PageImpl(bookings);

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(owner);

        Mockito
                .when(mockRepository.findAllByItemUserIdAndStatusNotOrderByStartDesc(owner.getId(),
                        BookingStatus.REJECTED, page))
                .thenReturn(pagedResponse);

        bookingService.getBookingByOwner(1, "FUTURE", 0, 10);

        Mockito.verify(mockRepository, Mockito.times(1))
                .findAllByItemUserIdAndStatusNotOrderByStartDesc(1, BookingStatus.REJECTED, page);

    }

    @Test
    @DisplayName("GetBookingByOwner - некорректное значение для пагинации")
    void testGetBookingByOwnerIncorrectPageNumber() {
        final IncorrectDataItemRequestExeption ex = assertThrows(
                IncorrectDataItemRequestExeption.class,
                () -> bookingService.getBookingByOwner(1, "ALL", -100, 10)
        );

        Assertions.assertEquals(ex.getMessage(), "некорректное значение параметров пагинации",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("GetBookingByOwner - некорректный статус")
    void testGetBookingByOwnerIncorrectState() {
        User booker = makeUser(1, "Jon", "jon@dow.com");

        Mockito
                .when(mockUserService.getUserById(Mockito.anyInt()))
                .thenReturn(booker);

        final IncorrectStatusBookingExeption ex = assertThrows(
                IncorrectStatusBookingExeption.class,
                () -> bookingService.getBookingByOwner(1, "LOVE", 0, 10)
        );

        Assertions.assertEquals(ex.getMessage(), "LOVE",
                "Ошибка не верная или не произошла");
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