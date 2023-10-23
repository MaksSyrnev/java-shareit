package ru.practicum.shareit.item.exeption;

public class IncorrectItemIdExeption extends RuntimeException {

    public IncorrectItemIdExeption(String message) {
        super(message);
    }

    public IncorrectItemIdExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectItemIdExeption(final Throwable cause) {
        super(cause);
    }
}
