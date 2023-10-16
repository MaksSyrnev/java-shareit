package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

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
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                     @PathVariable int bookingId, @RequestParam Boolean approved) {
        int userId = Integer.parseInt(headerUserId);
        return null;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                     @PathVariable int bookingId) {
        int userId = Integer.parseInt(headerUserId);
        return null;
    }

    @GetMapping
    public BookingDto getBookingByState(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                     @RequestParam(defaultValue = "ALL") String state) {
        int userId = Integer.parseInt(headerUserId);
        return null;
    }

    @GetMapping("/owner")
    public BookingDto getBookingByOwner(@RequestHeader("X-Sharer-User-Id") String headerUserId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        int userId = Integer.parseInt(headerUserId);
        return null;
    }

}
/**
 * - Добавление нового запроса на бронирование.
 * Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
 * Эндпоинт — POST /bookings. После создания запрос находится в статусе WAITING — «ожидает подтверждения».
 *
 * - Подтверждение или отклонение запроса на бронирование.
 * Может быть выполнено только владельцем вещи. Затем статус бронирования становится либо APPROVED, либо REJECTED.
 * Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать значения true или false.
 *
 * - Получение данных о конкретном бронировании (включая его статус).
 * Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование.
 * Эндпоинт — GET /bookings/{bookingId}.
 *
 * - Получение списка всех бронирований текущего пользователя.
 * Эндпоинт — GET /bookings?state={state}.
 * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
 * Также он может принимать значения CURRENT (англ. «текущие»), **PAST** (англ. «завершённые»),
 * FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
 * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
 *
 * - Получение списка бронирований для всех вещей текущего пользователя.
 * Эндпоинт — GET /bookings/owner?state={state}.
 * Этот запрос имеет смысл для владельца хотя бы одной вещи.
 * Работа параметра state аналогична его работе в предыдущем сценарии.
 */