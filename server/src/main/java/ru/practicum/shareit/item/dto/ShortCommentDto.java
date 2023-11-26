package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortCommentDto {
    private int id;
    private String text;
    private String authorName;
    @JsonFormat
    private LocalDateTime created;
}
