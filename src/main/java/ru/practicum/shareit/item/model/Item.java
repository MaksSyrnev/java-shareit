package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private int id;
    private User user;
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request;
}
