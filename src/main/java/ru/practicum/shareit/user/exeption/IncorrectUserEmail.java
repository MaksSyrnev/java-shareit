package ru.practicum.shareit.user.exeption;

public class IncorrectUserEmail extends RuntimeException {

    public IncorrectUserEmail(String message) {
        super(message);
    }

    public IncorrectUserEmail(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectUserEmail(final Throwable cause) {
        super(cause);
    }
}
