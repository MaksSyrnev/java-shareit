package ru.practicum.shareit.booking.exeptions;

public class IncorrectItemIdOrUserIdBoking extends RuntimeException {
    public IncorrectItemIdOrUserIdBoking(String message) {
        super(message);
    }

}
