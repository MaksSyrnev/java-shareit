package ru.practicum.shareit.booking.exeptions;

public class IncorrectStatusBookingExeption extends RuntimeException{
    public IncorrectStatusBookingExeption(String message) {
        super(message);
    }
}
