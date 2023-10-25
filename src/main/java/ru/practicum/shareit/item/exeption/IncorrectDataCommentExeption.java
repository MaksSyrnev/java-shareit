package ru.practicum.shareit.item.exeption;

public class IncorrectDataCommentExeption extends RuntimeException {
    public IncorrectDataCommentExeption(String message) {
        super(message);
    }

    public IncorrectDataCommentExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectDataCommentExeption(final Throwable cause) {
        super(cause);
    }
}
