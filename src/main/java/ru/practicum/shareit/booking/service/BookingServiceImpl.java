package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exeption.IncorrectUserIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

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
            throw new IncorrectUserIdException("Пользователь или вещь с таким id не найдены");
        }
        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (user.isPresent() && item.isPresent() && item.get().isAvailable()) {
            return repository.save(BookingMapper.toBooking(bookingDto, user.get(), item.get()));
        } else {
            throw new IncorrectUserIdException("Пользователь или вещь с таким id не найдены");
        }
    }

    @Override
    public Booking approveBooking(int userId, int bookingId, Boolean approved) {
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isPresent()) {
            int idOwner = booking.get().getItem().getUser().getId();
            if (idOwner != userId) {
                throw new IncorrectUserIdException("Пользователь или вещь с таким id не найдены");
            }
            if (approved) {
                booking.get().setStatus(BookingDtoState.APPROVED);
            } else {
                booking.get().setStatus(BookingDtoState.REJECTED);
                return repository.save(booking.get());
            }
        }
        throw new IncorrectUserIdException("нет такого id букинга");
    }

    @Override
    public BookingDto getBookingById(int userId, int bookingId) {
        return null;
    }

    @Override
    public BookingDto getBookingByState(int userId, String state) {
        return null;
    }

    @Override
    public BookingDto getBookingByOwner(int userId, String state) {
        return null;
    }

    private Boolean isUserExisting(int userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent();
    }

    private Boolean isValidDateBookingDto(BookingDto bookingDto) {
        if ( (bookingDto.getStart().isAfter(LocalDateTime.now())) &&
                (bookingDto.getEnd().isAfter(LocalDateTime.now()))) {
            return bookingDto.getEnd().isAfter(bookingDto.getStart());
        }
        return false;
    }
}
