package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortCommentDto {
    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
