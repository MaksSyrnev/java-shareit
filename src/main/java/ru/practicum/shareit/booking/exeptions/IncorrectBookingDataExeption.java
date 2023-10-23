package ru.practicum.shareit.booking.exeptions;

public class IncorrectBookingDataExeption extends RuntimeException {
    public IncorrectBookingDataExeption(String message) {
        super(message);
    }

    public IncorrectBookingDataExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectBookingDataExeption(final Throwable cause) {
        super(cause);
    }
}
