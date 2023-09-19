package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Override
    public User getUserById(int id) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public int deleteUserById(int id) {
        return 0;
    }

    @Override
    public int deleteAllUsers() {
        return 0;
    }
}
