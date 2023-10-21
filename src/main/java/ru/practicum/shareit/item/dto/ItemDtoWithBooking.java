package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemDtoWithBooking {
        private int id;
        private User user;
        private String name;
        private String description;
        private Boolean available;
        private ItemRequest request;
        private ShortBookingDto lastBooking;
        private ShortBookingDto nextBooking;
}
