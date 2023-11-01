package ru.practicum.shareit.user.exeption;

public class IncorrectUserIdException extends RuntimeException {
    public IncorrectUserIdException(String message) {
        super(message);
    }

}
