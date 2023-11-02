package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking addBooking(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                              @RequestBody BookingDto bookingDto) {
        log.debug("вызов эндпоинта POST /bookings, входящий json {} ", bookingDto);
        return bookingService.addBooking(headerUserId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                                     @PathVariable int bookingId, @RequestParam Boolean approved) {
        log.debug("вызов эндпоинта PATCH /bookings/{},  approved - {} ", bookingId, approved);
        return bookingService.approveBooking(headerUserId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                                     @PathVariable int bookingId) {
        log.debug("вызов эндпоинта GET /bookings/{}   ", bookingId);
        return bookingService.getBookingById(headerUserId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookingByState(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "20") @Positive int size) {
        log.debug("вызов эндпоинта GET /bookings/  с параметром - {}, from - {}, size- {} ", state, from, size);
        return bookingService.getBookingByState(headerUserId, state, from, size);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") int headerUserId,
                                        @RequestParam(defaultValue = "ALL") String state,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "20") @Positive int size) {
        log.debug("вызов эндпоинта GET /bookings/owner  с параметром - {}, from - {}, size- {} ", state, from, size);
        return bookingService.getBookingByOwner(headerUserId, state, from, size);
    }
}