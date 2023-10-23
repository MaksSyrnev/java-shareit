package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private int id;
    @JsonFormat
    private LocalDateTime start;
    @JsonFormat
    private LocalDateTime end;
    private int itemId;
    private int bookerId;
    private BookingStatus status;
}
