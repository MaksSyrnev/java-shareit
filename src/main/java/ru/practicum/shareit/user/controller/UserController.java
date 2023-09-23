package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 * создания, редактирования и просмотра.
 */

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
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return service.getUserById(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable int id, @RequestBody String name, @RequestBody String email) {
        return service.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        service.deleteUserById(id);
    }
}
