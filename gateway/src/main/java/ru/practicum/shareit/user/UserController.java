package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.user.dto.Marker;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("get users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable int id) {
        log.info("Get user with  userId={}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Validated({Marker.OnCreate.class}) UserDto userDto) {
        log.info("created user {} ", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable int userId,
            @Validated({Marker.OnUpdate.class}) @RequestBody UserDto userDto) {
        log.info("update user {}, userId={}", userDto, userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        log.info("delete user, userId={} ", id);
        return userClient.deleteUser(id);
    }
}
