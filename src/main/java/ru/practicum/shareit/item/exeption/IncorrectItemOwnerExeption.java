package ru.practicum.shareit.item.exeption;

public class IncorrectItemOwnerExeption extends RuntimeException{

    public IncorrectItemOwnerExeption(String message) {
        super(message);
    }
}
