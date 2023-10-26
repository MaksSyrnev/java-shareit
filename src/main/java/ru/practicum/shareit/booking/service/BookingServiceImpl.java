package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exeptions.IncorrectBookingDataExeption;
import ru.practicum.shareit.booking.exeptions.IncorrectItemIdOrUserIdBoking;
import ru.practicum.shareit.booking.exeptions.IncorrectStatusBookingExeption;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository repository, ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Booking addBooking(int userId, BookingDto bookingDto) {
        log.info("+ addBooking: {}", bookingDto);
        bookingDto.setStatus(BookingStatus.WAITING);
        if (!isValidDateBookingDto(bookingDto)) {
            throw new IncorrectBookingDataExeption("Даты бронирования некорректные");
        }
        User booker = userRepository.findById(userId).orElseThrow(
                () -> new IncorrectItemIdOrUserIdBoking("Пользователь с таким id не найден"));

        Item itemBooking = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new IncorrectItemIdOrUserIdBoking("вещь с таким id не найдены"));
        if (booker.getId() == itemBooking.getUser().getId()) {
            throw new IncorrectItemIdOrUserIdBoking("Пользователь и владелец совпадают по id");
        } else if (!itemBooking.isAvailable()) {
            throw new IncorrectBookingDataExeption("Вещь недоступна к бронированию");
        }
        Booking newBooking = BookingMapper.toBooking(bookingDto, booker, itemBooking);
        return repository.save(newBooking);
    }

    @Override
    public Booking approveBooking(int userId, int bookingId, Boolean approved) {
        Booking booking = repository.findById(bookingId).orElseThrow(
                () -> new IncorrectItemIdOrUserIdBoking("Букинг с таким id не найден"));
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
        Booking booking = repository.findById(bookingId).orElseThrow(
                () -> new IncorrectItemIdOrUserIdBoking("Букинг с таким id не найден"));
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
    public List<Booking> getBookingByState(int userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IncorrectItemIdOrUserIdBoking("Пользователь с таким id не найдены"));
        log.info("+ getBookingByState: юзер - {}, найден - {}, статус - {}", userId, user, state);
        switch (state) {
            case "CURRENT":
                return repository.findAllByBookerIdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED).stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllByBookerIdAndStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED)
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case "ALL":
                return repository.findAllByBookerIdOrderByStartDesc(userId);
            default:
                throw new IncorrectStatusBookingExeption(state);
        }
    }

    @Override
    public List<Booking> getBookingByOwner(int userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IncorrectItemIdOrUserIdBoking("Пользователь с таким id не найден"));
        switch (state) {
            case "CURRENT":
                return repository.findAllByItemUserIdOrderByStartDesc(userId)
                        .stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED)
                        .stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllByItemUserIdAndStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED)
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case "ALL":
                return repository.findAllByItemUserIdOrderByStartDesc(userId);
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
