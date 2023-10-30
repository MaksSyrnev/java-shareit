package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exeption.IncorrectUserIdException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class UserServiceImplTest {
    private final UserRepository mockRepository = Mockito.mock(UserRepository.class);

    final UserServiceImpl userService = new UserServiceImpl(mockRepository);

    @Test
    @DisplayName("GetUserById - вызов метода реопозитория")
    void testGetUserById() {
        User user = makeUser(1, "Jon", "jon@dow.com");

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                        .thenReturn(Optional.of(user));

        userService.getUserById(1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .findById(Mockito.anyInt());

    }

    @Test
    @DisplayName("GetUserById - пользователь не найден")
    void testGetUserByIdNotFound() {
        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final IncorrectUserIdException ex = assertThrows(
                IncorrectUserIdException.class,
                () -> userService.getUserById(1)
        );

        Assertions.assertEquals(ex.getMessage(), "Пользователь с таким id не найден",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("GetAllUsers - вызов метода реопозитория")
    void testGetAllUsers() {
        User user = makeUser(1, "Jon", "jon@dow.com");

        Mockito
                .when(mockRepository.findAll())
                .thenReturn(List.of(user));

        userService.getAllUsers();

        Mockito.verify(mockRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    @DisplayName("AddUser - мапинг dto в сущность и вызов метода реопозитория")
    void testAddUser() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Jon Sena");
        newUserDto.setEmail("jon-sena@java.com");
        User user = makeUser(0, "Jon Sena", "jon-sena@java.com");

        Mockito
                .when(mockRepository.save(Mockito.any()))
                .thenReturn(user);

        userService.addUser(newUserDto);

        Mockito.verify(mockRepository, Mockito.times(1))
                .save(user);
    }

    @Test
    @DisplayName("UpdateUser - вызов 2 методов реопозитория")
    void testUpdateUser() {
        User user = makeUser(1, "Jon Sena", "jon-sena@java.com");
        UserDto userDto = new UserDto();
        userDto.setName("Sena");

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(mockRepository.save(Mockito.any()))
                .thenReturn(user);

        userService.updateUser(1, userDto);

        Mockito.verify(mockRepository, Mockito.times(1))
                .findById(1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .save(user);

    }

    @Test
    @DisplayName("UpdateUser - пользователь не найден")
    void testUpdateUserNotFound() {
        UserDto userDto = new UserDto();
        userDto.setName("Sena");

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final IncorrectUserIdException ex = assertThrows(
                IncorrectUserIdException.class,
                () -> userService.updateUser(1, userDto)
        );

        Assertions.assertEquals(ex.getMessage(), "нет пользователя с таким id",
                "Ошибка не верная или не произошла");

        Mockito.verify(mockRepository, Mockito.times(1))
                .findById(1);

        Mockito.verify(mockRepository, Mockito.times(0))
                .save(Mockito.any());

    }

    @Test
    @DisplayName("UpdateUser - обновляет имя у сущности")
    void testUpdateUserName() {
        User user = makeUser(1, "Jon Sena", "jon-sena@java.com");
        UserDto userDto = new UserDto();
        userDto.setName("Sena");

        Mockito
                .when(mockRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        userService.updateUser(1, userDto);

        Assertions.assertEquals(user.getName(), "Sena", "у сущности не обновилось имя из dto");
    }

    private User makeUser(int id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

}
