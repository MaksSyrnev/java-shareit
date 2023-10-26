package ru.practicum.shareit.request.exeption;

public class IncorrectIdRequestExeption extends RuntimeException {
    public IncorrectIdRequestExeption(String message) {
        super(message);
    }

    public IncorrectIdRequestExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectIdRequestExeption(final Throwable cause) {
        super(cause);
    }
}
