package ru.practicum.shareit.user.exeption;

public class IncorrectUserEmail extends RuntimeException {

    public IncorrectUserEmail(String message) {
        super(message);
    }
}
