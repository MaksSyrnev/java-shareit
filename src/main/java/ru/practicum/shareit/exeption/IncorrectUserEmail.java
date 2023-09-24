package ru.practicum.shareit.exeption;

public class IncorrectUserEmail extends RuntimeException {

    public IncorrectUserEmail(String message) {
        super(message);
    }
}
