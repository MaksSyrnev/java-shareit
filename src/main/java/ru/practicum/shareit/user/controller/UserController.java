package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 * создания, редактирования и просмотра.
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService userService) {
        this.service = userService;
    }

    @GetMapping
    public List<User> getAllUser() {
        log.info("Запрос к эндпоинту: GET /users ");
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Запрос к эндпоинту: GET /users/'{}' ", id);
        return service.getUserById(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Запрос к эндпоинту: POST /users/ , данные запроса - {} ", newUserDto);
        return service.addUser(newUserDto);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable int id, @RequestBody UserDto userDto) {
        log.info("Запрос к эндпоинту: PATCH /users/'{}' ", id);
        log.info("Данные для обновления: '{}' ", userDto);
        return service.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Запрос к эндпоинту: DELETE /users/'{}' ", id);
        service.deleteUserById(id);
    }
}
