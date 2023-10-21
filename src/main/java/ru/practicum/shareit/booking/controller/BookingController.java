package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking addBooking(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                              @RequestBody BookingDto bookingDto) {
        log.info("вызов эндпоинта POST /bookings, входящий json {} ", bookingDto);
        int userId = Integer.parseInt(headerUserId);
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                     @PathVariable int bookingId, @RequestParam Boolean approved) {
        log.info("вызов эндпоинта PATCH /bookings/{},  approved - {} ", bookingId, approved);
        int userId = Integer.parseInt(headerUserId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                     @PathVariable int bookingId) {
        log.info("вызов эндпоинта GET /bookings/{}   ", bookingId);
        int userId = Integer.parseInt(headerUserId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookingByState(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        int userId = Integer.parseInt(headerUserId);
        return bookingService.getBookingByState(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        int userId = Integer.parseInt(headerUserId);
        return bookingService.getBookingByOwner(userId, state);
    }

}