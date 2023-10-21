package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking addBooking(int userId, BookingDto bookingDto);

    Booking approveBooking(int userId, int bookingId, Boolean approved);

    Booking getBookingById(int userId, int bookingId);

    List<Booking> getBookingByState(int userId, String state);

    List<Booking> getBookingByOwner(int userId, String state);

}
