package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserById(int id);

    List<User> getAllUsers();

    User addUser(NewUserDto user);

    User updateUser(int id, UserDto userDto);

    void deleteUserById(int id);
}
