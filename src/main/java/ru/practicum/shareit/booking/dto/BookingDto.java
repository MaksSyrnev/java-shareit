package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
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
    private String status;
}
