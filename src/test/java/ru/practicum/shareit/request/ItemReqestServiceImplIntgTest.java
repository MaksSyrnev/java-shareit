package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemReqestServiceImplIntgTest {
    private final EntityManager em;
    private final ItemRequestServiceImpl service;

    @Test
    void getAllRequest() {
        NewUserDto userOne = makeNewUserDto("Миша", "mi6a@mail.ru");
        NewUserDto userTwo = makeNewUserDto("Маша", "ma6a@mail.ru");
        User owner = UserMapper.toUser(userOne);
        User requestor = UserMapper.toUser(userTwo);
        em.persist(owner);
        em.persist(requestor);

        ItemRequest requestOne = makeItemRequest("textOne", requestor, LocalDateTime.now().minusDays(1));
        em.persist(requestOne);
        ItemRequest requestTwo = makeItemRequest("textTwo", requestor, LocalDateTime.now());
        em.persist(requestTwo);
        ItemRequest requestTree = makeItemRequest("text", owner, LocalDateTime.now());
        em.persist(requestTree);

        em.flush();

        List<ItemRequestWithItemsDto> targetRequests = service.getAllRequest(owner.getId(), 0, 10);

        assertThat(targetRequests, hasSize(2));
        assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue())
            )));
    }

    private ItemRequest makeItemRequest(String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        return itemRequest;
    }

    private NewUserDto makeNewUserDto(String name, String email) {
        NewUserDto newUser = new NewUserDto();
        newUser.setName(name);
        newUser.setEmail(email);
        return newUser;
    }
}
