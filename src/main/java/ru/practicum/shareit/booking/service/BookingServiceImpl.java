package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

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
        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        log.info("букинг пишел такой {}", bookingDto);
        return null;
    }

    @Override
    public BookingDto approveBooking(int userId, int bookingId, Boolean approved) {
        return null;
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
}
