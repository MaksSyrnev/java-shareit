package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exeption.IncorrectUserIdException;
import ru.practicum.shareit.user.exeption.IncorrectUserEmail;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
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
            log.error("getUserById -  {}, неверный id", id);
            throw new IncorrectUserIdException("Пользователь с таким id не найден");
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
            log.error("addUser, почта уже есть в сервисе -  {} ", user.getEmail());
            throw new IncorrectUserEmail("почта уже зарегистрирована в сервисе");
        }
        return storage.addUser(user);
    }

    @Override
    public User updateUser(int id, UserDto userDto) {
        Optional<String> name = Optional.ofNullable(userDto.getName());
        Optional<String> email = Optional.ofNullable(userDto.getEmail());
        Optional<User> usrStorage = storage.getUser(id);
        if(usrStorage.isEmpty()) {
            log.error("updateUser -  {}, неверный id", id);
            throw new IncorrectUserIdException("нет пользователя с таким id");
        }
        if(name.isPresent()) {
            usrStorage.get().setName(name.get());
        }
        if(email.isPresent()) {
            boolean isEmailSome = usrStorage.get().getEmail().equals(email.get());
            if(isNotUniqEmail(email.get()) && !isEmailSome) {
                log.error("updateUser, почта уже есть в сервисе -  {} ", email.get());
                throw new IncorrectUserEmail("почта уже зарегистрирована в сервисе");
            }
            usrStorage.get().setEmail(email.get());
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
