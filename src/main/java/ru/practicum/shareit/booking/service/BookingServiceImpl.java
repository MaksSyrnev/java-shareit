package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exeptions.IncorrectBookingDataExeption;
import ru.practicum.shareit.booking.exeptions.IncorrectItemIdOrUserIdBoking;
import ru.practicum.shareit.booking.exeptions.IncorrectStatusBookingExeption;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.exeption.IncorrectDataItemRequestExeption;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.PageRequest.of;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl(BookingRepository repository, ItemService itemService, UserService userService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public Booking addBooking(int userId, BookingDto bookingDto) {
        log.info("+ addBooking: {}", bookingDto);
        bookingDto.setStatus(BookingStatus.WAITING);
        if (!isValidDateBookingDto(bookingDto)) {
            throw new IncorrectBookingDataExeption("Даты бронирования некорректные");
        }
        User booker = userService.getUserById(userId);
        ItemDtoWithBooking itemBooking = itemService.getItemById(userId, bookingDto.getItemId());
        Item item  = toItem(itemBooking);
        if (booker.getId() == item.getUser().getId()) {
            throw new IncorrectItemIdOrUserIdBoking("Пользователь и владелец совпадают по id");
        } else if (!itemBooking.getAvailable()) {
            throw new IncorrectBookingDataExeption("Вещь недоступна к бронированию");
        }
        Booking newBooking = BookingMapper.toBooking(bookingDto, booker, item);
        return repository.save(newBooking);
    }

    @Override
    public Booking approveBooking(int userId, int bookingId, Boolean approved) {
        Optional<Booking> finedBooking = repository.findById(bookingId);
        if (finedBooking.isEmpty()) {
            throw new IncorrectItemIdOrUserIdBoking("Букинг с таким id не найден");
        }
        Booking booking = finedBooking.get();
        log.info("+ approveBooking: {}, findBooking: {}, userID: {}", bookingId, booking, userId);
        int idOwner = booking.getItem().getUser().getId();
        if (idOwner != userId) {
            throw new IncorrectItemIdOrUserIdBoking("Доступ запрещен");
        }
        if (approved) {
            BookingStatus currentStatus = booking.getStatus();
            if (currentStatus != BookingStatus.APPROVED) {
                booking.setStatus(BookingStatus.APPROVED);
                return repository.save(booking);
            }
            throw new IncorrectBookingDataExeption("Бронирование уже подтверждено");
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            return repository.save(booking);
        }
    }

    @Override
    public Booking getBookingById(int userId, int bookingId) {
        Optional<Booking> finedBooking = repository.findById(bookingId);
        if (finedBooking.isEmpty()) {
            throw new IncorrectItemIdOrUserIdBoking("Букинг с таким id не найден");
        }
        Booking booking = finedBooking.get();
        log.info("+ getBookingById: букинг - {}, найден -  {}", bookingId, booking);
        int idOwner = booking.getItem().getUser().getId();
        int idBooker = booking.getBooker().getId();
        if ((idOwner == userId) || (idBooker == userId)) {
             return booking;
        } else {
            throw new IncorrectItemIdOrUserIdBoking("Доступ запрещен");
        }
    }

    @Override
    public List<Booking> getBookingByState(int userId, String state, int from, int size) {
        if ((from < 0) || (size < 0)) {
            throw new IncorrectDataItemRequestExeption("некорректное значение параметров пагинации");
        }
        User user = userService.getUserById(userId);
        log.info("+ getBookingByState: юзер - {}, найден - {}, статус - {}", userId, user, state);
        PageRequest page = of(from > 0 ? from / size : 0, size);
        switch (state) {
            case "CURRENT":
                return repository.findAllByBookerIdOrderByStartDesc(userId, page)
                        .getContent()
                        .stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED, page)
                        .getContent()
                        .stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllByBookerIdAndStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED, page)
                        .getContent()
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page)
                        .getContent();
            case "REJECTED":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page)
                        .getContent();
            case "ALL":
                return repository.findAllByBookerIdOrderByStartDesc(userId, page).getContent();
            default:
                throw new IncorrectStatusBookingExeption(state);
        }
    }

    @Override
    public List<Booking> getBookingByOwner(int userId, String state, int from, int size) {
        if ((from < 0) || (size < 0)) {
            throw new IncorrectDataItemRequestExeption("некорректное значение параметров пагинации");
        }
        User user = userService.getUserById(userId);
        PageRequest page = of(from > 0 ? from / size : 0, size);
        switch (state) {
            case "CURRENT":
                return repository.findAllByItemUserIdOrderByStartDesc(userId, page)
                        .getContent()
                        .stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED, page)
                        .getContent()
                        .stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllByItemUserIdAndStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED, page)
                        .getContent()
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page)
                        .getContent();
            case "REJECTED":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page)
                        .getContent();
            case "ALL":
                return repository.findAllByItemUserIdOrderByStartDesc(userId, page)
                        .getContent();
            default:
                throw new IncorrectStatusBookingExeption(state);
        }
    }

    private Boolean isValidDateBookingDto(BookingDto bookingDto) {
        Optional<LocalDateTime> start = Optional.ofNullable(bookingDto.getStart());
        Optional<LocalDateTime> end = Optional.ofNullable(bookingDto.getEnd());
        if (start.isEmpty() || end.isEmpty()) {
            return false;
        }
        if ((bookingDto.getStart().isAfter(LocalDateTime.now())) &&
                (bookingDto.getEnd().isAfter(LocalDateTime.now()))) {
            return bookingDto.getEnd().isAfter(bookingDto.getStart());
        }
        return false;
    }
}
