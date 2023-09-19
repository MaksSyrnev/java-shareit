package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User getUserById(int id);

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    int deleteUserById(int id);

    int deleteAllUsers();
}
