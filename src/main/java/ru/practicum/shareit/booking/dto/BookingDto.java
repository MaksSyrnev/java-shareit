package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private int id;
    private Instant start;
    private Instant end;
    private Item item;
    private User booker;
    private String status;
}
