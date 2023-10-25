package ru.practicum.shareit.item.exeption;

public class IncorrectItemOwnerExeption extends RuntimeException {

    public IncorrectItemOwnerExeption(String message) {
        super(message);
    }

    public IncorrectItemOwnerExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectItemOwnerExeption(final Throwable cause) {
        super(cause);
    }
}
