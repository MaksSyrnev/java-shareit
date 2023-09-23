package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.IncorrectIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public User getUserById(int id) {
        Optional<User> usrStorage = storage.getUser(id);
        if(usrStorage.isEmpty()) {
            throw new IncorrectIdException("нет пользователя с таким id");
        }
        return usrStorage.get();
    }

    @Override
    public List<User> getAllUsers() {
        return storage.getAll();
    }

    @Override
    public User addUser(User user) {
        String email = user.getEmail();
        if(isNotUniqEmail(email)) {
            throw new IncorrectIdException("нет пользователя с таким id");
        }
        return storage.addUser(user);
    }

    @Override
    public User updateUser(int id, User user) {
        Optional<User> usrStorage = storage.updateUser(id,user);
        if(usrStorage.isEmpty()) {
            throw new IncorrectIdException("нет пользователя с таким id");
        }
        return usrStorage.get();
    }

    @Override
    public int deleteUserById(int id) {
        return storage.deleteUser(id);
    }

    @Override
    public int deleteAllUsers() {
        return storage.deleteAll();
    }

    private boolean isNotUniqEmail(String email) {
        List<User> users = getAllUsers();
        for(User user: users) {
            if(email.equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
