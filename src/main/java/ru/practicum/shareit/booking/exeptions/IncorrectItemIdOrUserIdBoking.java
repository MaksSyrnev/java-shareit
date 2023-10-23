package ru.practicum.shareit.booking.exeptions;

public class IncorrectItemIdOrUserIdBoking extends RuntimeException {
    public IncorrectItemIdOrUserIdBoking(String message) {
        super(message);
    }

    public IncorrectItemIdOrUserIdBoking(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectItemIdOrUserIdBoking(final Throwable cause) {
        super(cause);
    }
}
