package ru.practicum.shareit.booking.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {
    Booking addBooking(int userId, BookingDto bookingDto);
    Booking approveBooking(int userId, int bookingId, Boolean approved);
    BookingDto getBookingById(int userId, int bookingId);
    BookingDto getBookingByState(int userId, String state);
    BookingDto getBookingByOwner(int userId, String state);
}
