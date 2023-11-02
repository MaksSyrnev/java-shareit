package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.data.domain.PageRequest.of;

@Slf4j
@Transactional
@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemReopositoryTest {
    @Autowired
    private final TestEntityManager em;

    @Autowired
    private final ItemRepository repository;

    @Test
    void search() {
        NewUserDto newUserDto = makeNewUserDto("Миша", "mi6a@mail.ru");
        User entity = UserMapper.toUser(newUserDto);
        em.persist(entity);
        log.info(" user - {}",entity);

        PageRequest page = of(0, 10);

        List<Item> sourceItems = List.of(
                makeItem("One", "text", entity, Boolean.TRUE),
                makeItem("Two", "text-two", entity, Boolean.TRUE)
        );

        for (Item item : sourceItems) {
            em.persist(item);
        }
        em.flush();

        Page<Item> targetItems = repository.search("text", page);
        List<Item> items = targetItems.toList();

        assertThat(items, hasSize(sourceItems.size()));
        for (Item sourceItem : sourceItems) {
            assertThat(items, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription()))
            )));
        }
    }

    private NewUserDto makeNewUserDto(String name, String email) {
        NewUserDto newUser = new NewUserDto();
        newUser.setName(name);
        newUser.setEmail(email);
        return newUser;
    }

    private Item makeItem(String name, String description, User user, Boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setUser(user);
        item.setDescription(description);
        item.setAvailable(available);
        return item;
    }
}
