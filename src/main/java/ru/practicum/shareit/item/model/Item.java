package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;
    @NotNull
    @Column(name = "is_available", nullable = false)
    private boolean available;
    @ManyToOne
    @JoinColumn(name = "request_id", nullable = true)
    private ItemRequest request;
}
