package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private int id;
    private User user;
    private String name;
    private String description;
    private String available;
    private ItemRequest request;
}
