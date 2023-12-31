package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentReopository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemId(int itemId);
}
