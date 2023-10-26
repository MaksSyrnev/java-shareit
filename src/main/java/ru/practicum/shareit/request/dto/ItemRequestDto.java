package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private int id;
    @NotBlank
    private String description;
    private User requestor;
}
