package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User getUser(int id);

    List<User> getAll();

    int deleteUser(int id);

    int deleteAll();

    User updateUser(User user);
}
