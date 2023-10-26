package ru.practicum.shareit.request.exeption;

public class IncorrectDataItemRequestExeption extends RuntimeException {
    public IncorrectDataItemRequestExeption(String message) {
        super(message);
    }

    public IncorrectDataItemRequestExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectDataItemRequestExeption(final Throwable cause) {
        super(cause);
    }
}
