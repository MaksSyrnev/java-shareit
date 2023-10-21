package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exeption.IncorrectUserIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User getUserById(int id) {
        Optional<User> usrStorage = repository.findById(id);
        if (usrStorage.isEmpty()) {
            log.error("getUserById -  {}, неверный id", id);
            throw new IncorrectUserIdException("Пользователь с таким id не найден");
        }
        return usrStorage.get();
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User addUser(User user) {
        return repository.save(user);
    }

    @Override
    public User updateUser(int id, UserDto userDto) {
        Optional<String> name = Optional.ofNullable(userDto.getName());
        Optional<String> email = Optional.ofNullable(userDto.getEmail());
        Optional<User> usrStorage = repository.findById(id);
        if (usrStorage.isEmpty()) {
            log.error("updateUser -  {}, неверный id", id);
            throw new IncorrectUserIdException("нет пользователя с таким id");
        }
        if (name.isPresent()) {
            usrStorage.get().setName(name.get());
        }
        if (email.isPresent()) {
            usrStorage.get().setEmail(email.get());
        }
        repository.save(usrStorage.get());
        return usrStorage.get();
    }

    @Override
    public void deleteUserById(int id) {
        Optional<User> user = repository.findById(id);
        if(user.isPresent()) {
            repository.delete(user.get());
        }
    }

    @Override
    public void deleteAllUsers() {
        repository.deleteAll();
    }

}
