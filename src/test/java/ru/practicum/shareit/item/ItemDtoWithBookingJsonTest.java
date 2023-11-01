package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ShortCommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@JsonTest
public class ItemDtoWithBookingJsonTest {
    @Autowired
    private JacksonTester<ItemDtoWithBooking> json;

    @Test
    void testUserDto() throws Exception {
        User owner = makeUser(2, "Jony Mnemonick", "jony@dow.com");
        ItemRequest request = makeItemRequest(1, "запрос на балдеж", owner, LocalDateTime.now());
        ShortBookingDto lastBooking = makeShortBookingDto(11, 12);
        ShortBookingDto nextBooking = makeShortBookingDto(12, 13);
        ShortCommentDto comentOne = makeShortCommentDto(1, "неплохо", "Bill Simple",
                LocalDateTime.now().minusDays(1));
        ShortCommentDto comentTwo = makeShortCommentDto(2, "хорошо! огонь!", "Bill More",
                LocalDateTime.now().minusDays(2));
        List<ShortCommentDto> coments = List.of(comentOne, comentTwo);
        ItemDtoWithBooking itemDtoWithBooking = makeItemDtoWithBooking(1, owner,
                "Вещь", "Обалденная",true, request, lastBooking, nextBooking,
                coments);

        JsonContent<ItemDtoWithBooking> result = json.write(itemDtoWithBooking);
        log.info("result{}", result);
    }

    private User makeUser(int id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemDtoWithBooking makeItemDtoWithBooking(int id, User user, String name, String description,
                                                      Boolean available, ItemRequest request,
                                                      ShortBookingDto lastBooking, ShortBookingDto nextBooking,
                                                      List<ShortCommentDto> comments) {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setId(id);
        itemDtoWithBooking.setUser(user);
        itemDtoWithBooking.setName(name);
        itemDtoWithBooking.setDescription(description);
        itemDtoWithBooking.setAvailable(available);
        itemDtoWithBooking.setRequest(request);
        itemDtoWithBooking.setLastBooking(lastBooking);
        itemDtoWithBooking.setNextBooking(nextBooking);
        itemDtoWithBooking.setComments(comments);
        return itemDtoWithBooking;
    }

    private ShortCommentDto makeShortCommentDto(int id, String text, String authorName, LocalDateTime created) {
        ShortCommentDto shortCommentDto = new ShortCommentDto();
        shortCommentDto.setId(id);
        shortCommentDto.setText(text);
        shortCommentDto.setAuthorName(authorName);
        shortCommentDto.setCreated(created);
        return shortCommentDto;
    }

    private ItemRequest makeItemRequest(int id, String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        return itemRequest;
    }

    private ShortBookingDto makeShortBookingDto(int id, int bookerId) {
        ShortBookingDto shortBookingDto = new ShortBookingDto();
        shortBookingDto.setId(id);
        shortBookingDto.setBookerId(bookerId);
        return shortBookingDto;
    }
}
