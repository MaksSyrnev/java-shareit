package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 * создания, редактирования и просмотра.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserService service;
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
    public User addUser(User user) {
        return service.addUser(user);
    }

    @PatchMapping("/{id}")
    public User updateUser(User user) {
        return service.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        service.deleteUserById(id);
    }
}
