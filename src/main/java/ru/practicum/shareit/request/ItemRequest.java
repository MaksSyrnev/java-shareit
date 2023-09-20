package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    int id;
    String description;
    User requestor;
    Timestamp created;
}
