package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        Booking boking = new Booking();
        boking.setStart(bookingDto.getStart());
        boking.setEnd(bookingDto.getEnd());
        boking.setItem(item);
        boking.setBooker(user);
        boking.setStatus(bookingDto.getStatus());
        return boking;
    }

    public static ShortBookingDto toShortBookingDto(Booking booking) {
        ShortBookingDto shortBookingDto = new ShortBookingDto();
        shortBookingDto.setId(booking.getId());
        shortBookingDto.setBookerId(booking.getBooker().getId());
        return shortBookingDto;
    }
}
