package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private Timestamp start;
    private Timestamp end;
    private Item item;
    private User booker;
    private String status;
}
