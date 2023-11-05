package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public static ShortCommentDto toShortCommentDto(Comment comment) {
        ShortCommentDto shortCommentDto = new ShortCommentDto();
        shortCommentDto.setId(comment.getId());
        shortCommentDto.setText(comment.getText());
        shortCommentDto.setAuthorName(comment.getAuthor().getName());
        shortCommentDto.setCreated(comment.getCreated());
        return shortCommentDto;
    }
}
