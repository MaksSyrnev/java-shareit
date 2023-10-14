package ru.practicum.shareit.item.model;

import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "comment_date")
    private Instant comment_date = Instant.now();
}
