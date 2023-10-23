package ru.practicum.shareit.booking.exeptions;

public class IncorrectStatusBookingExeption extends RuntimeException {
    public IncorrectStatusBookingExeption(String message) {
        super(message);
    }

    public IncorrectStatusBookingExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectStatusBookingExeption(final Throwable cause) {
        super(cause);
    }
}
