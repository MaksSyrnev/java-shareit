package ru.practicum.shareit.item.exeption;

public class IncorrectItemDataExeption extends RuntimeException {

    public IncorrectItemDataExeption(String message) {
        super(message);
    }

    public IncorrectItemDataExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectItemDataExeption(final Throwable cause) {
        super(cause);
    }
}
