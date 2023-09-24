package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return user;
    }
}
