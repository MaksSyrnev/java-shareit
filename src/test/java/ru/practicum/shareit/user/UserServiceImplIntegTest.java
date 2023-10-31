package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    @DisplayName("saveUser - интеграция")
    void saveUser() {
        NewUserDto newUserDto = makeNewUserDto("Миша", "mi6a@mail.ru");

        service.addUser(newUserDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUserDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUserDto.getName()));
        assertThat(user.getEmail(), equalTo(newUserDto.getEmail()));
    }

    @Test
    @DisplayName("getAllUsers - интеграция")
    void getAllUsers() {
        List<NewUserDto> sourceUsers = List.of(
                makeNewUserDto("Ivan","ivan@email"),
                makeNewUserDto("Vasilii","vasilii@email")
        );

        for (NewUserDto user : sourceUsers) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        List<User> targetUsers = service.getAllUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (NewUserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    private NewUserDto makeNewUserDto(String name, String email) {
        NewUserDto newUser = new NewUserDto();
        newUser.setName(name);
        newUser.setEmail(email);
        return newUser;
    }
}
