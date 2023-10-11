package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    Optional<User> getUser(int id);

    List<User> getAll();

    int deleteUser(int id);

    int deleteAll();

    Optional<User> updateUser(int id, User user);
}
