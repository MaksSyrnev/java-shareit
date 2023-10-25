package ru.practicum.shareit.user.exeption;

public class IncorrectUserIdException extends RuntimeException {
    public IncorrectUserIdException(String message) {
        super(message);
    }

    public IncorrectUserIdException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectUserIdException(final Throwable cause) {
        super(cause);
    }
}
