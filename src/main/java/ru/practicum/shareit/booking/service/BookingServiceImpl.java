package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoState;
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
        log.info("букинг пришел такой {}", bookingDto);
        bookingDto.setStatus(BookingDtoState.WAITING);
        if (!isValidDateBookingDto(bookingDto)) {
            throw new IncorrectBookingDataExeption("Даты бронирования некорректные");
        }
        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (user.isEmpty() || item.isEmpty() || (user.get().getId() == item.get().getUser().getId())) {
            throw new IncorrectItemIdOrUserIdBoking("Пользователь или вещь с таким id не найдены");
        } else if (!item.get().isAvailable()) {
            throw new IncorrectBookingDataExeption("Вещь недоступна к бронированию");
        }
        return repository.save(BookingMapper.toBooking(bookingDto, user.get(), item.get()));
    }

    @Override
    public Booking approveBooking(int userId, int bookingId, Boolean approved) {
        Optional<Booking> booking = repository.findById(bookingId);
        log.info("сервис апрув букинга - {}, найден  - {} ", bookingId, booking.isPresent());
        if (booking.isPresent()) {
            int idOwner = booking.get().getItem().getUser().getId();
            if (idOwner != userId) {
                throw new IncorrectItemIdOrUserIdBoking("Доступ запрещен");
            }
            if (approved) {
                BookingDtoState currentStatus = booking.get().getStatus();
                if (!(currentStatus.name() == "APPROVED")) {
                    booking.get().setStatus(BookingDtoState.APPROVED);
                    return repository.save(booking.get());
                }
                throw new IncorrectBookingDataExeption("Бронирование уже подтверждено");
            } else {
                booking.get().setStatus(BookingDtoState.REJECTED);
                return repository.save(booking.get());
            }
        } else {
            throw new IncorrectItemIdOrUserIdBoking("нет такого id букинга");
        }

    }

    @Override
    public Booking getBookingById(int userId, int bookingId) {
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isPresent()) {
            int idOwner = booking.get().getItem().getUser().getId();
            int idBooker = booking.get().getBooker().getId();
            if ((idOwner == userId) || (idBooker == userId)) {
                return booking.get();
            } else {
                throw new IncorrectItemIdOrUserIdBoking("Доступ запрещен");
            }
        }
        throw new IncorrectItemIdOrUserIdBoking("нет такого id букинга");
    }

    @Override
    public List<Booking> getBookingByState(int userId, String state) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IncorrectItemIdOrUserIdBoking("Пользователь или вещь с таким id не найдены");
        }
        switch (state) {
            case "CURRENT":
                return repository.findAllByBookerIdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingDtoState.APPROVED).stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllByBookerIdAndStatusNotOrderByStartDesc(userId, BookingDtoState.REJECTED)
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingDtoState.WAITING);
            case "REJECTED":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingDtoState.REJECTED);
            case "ALL":
                return repository.findAllByBookerIdOrderByStartDesc(userId);
            default:
                throw new IncorrectStatusBookingExeption(state);
        }
    }

    @Override
    public List<Booking> getBookingByOwner(int userId, String state) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IncorrectItemIdOrUserIdBoking("Пользователь или вещь с таким id не найдены");
        }
        switch (state) {
            case "CURRENT":
                return repository.findAllByItemUserIdOrderByStartDesc(userId)
                        .stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingDtoState.APPROVED)
                        .stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return repository.findAllByItemUserIdAndStatusNotOrderByStartDesc(userId, BookingDtoState.REJECTED)
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingDtoState.WAITING);
            case "REJECTED":
                return repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingDtoState.REJECTED);
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
