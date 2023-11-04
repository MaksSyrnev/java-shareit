package ru.practicum.shareit.request.exeption;

public class IncorrectIdRequestExeption extends RuntimeException {
    public IncorrectIdRequestExeption(String message) {
        super(message);
    }
}
