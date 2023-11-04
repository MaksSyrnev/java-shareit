package ru.practicum.shareit.booking.exeptions;

public class IncorrectBookingDataExeption extends RuntimeException {
    public IncorrectBookingDataExeption(String message) {
        super(message);
    }
}
