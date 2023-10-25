package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int itemId;
    private int bookerId;
    private BookingStatus status;
}
