package ru.practicum.shareit.request.exeption;

public class IncorrectDataItemRequestExeption extends RuntimeException {
    public IncorrectDataItemRequestExeption(String message) {
        super(message);
    }
}
