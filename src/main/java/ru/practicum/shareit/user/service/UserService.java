package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserById(int id);

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(int id, User user);

    int deleteUserById(int id);

    int deleteAllUsers();
}
