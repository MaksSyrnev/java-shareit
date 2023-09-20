package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    int id;
    User user;
    String name;
    String description;
    boolean availability;
    ItemRequest request;
}
