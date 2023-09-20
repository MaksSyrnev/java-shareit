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
    int id;
    Timestamp start;
    Timestamp end;
    Item item;
    User booker;
    String status;
}
