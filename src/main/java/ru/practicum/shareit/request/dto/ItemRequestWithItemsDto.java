package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestWithItemsDto {
    private int id;
    @NotBlank
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
