package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ShortCommentDto;
import ru.practicum.shareit.item.exeption.IncorrectItemDataExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemIdExeption;
import ru.practicum.shareit.item.exeption.IncorrectItemOwnerExeption;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.dto.BookingMapper.toShortBookingDto;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemService service;

    @Test
    void addItem() throws Exception {
        ItemDto itemDto = makeItemDto("Text", "Text text", true);

        when(service.addItem(1, itemDto))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void addItemBlankName() throws Exception {
        ItemDto itemDto = makeItemDto(" ", "Text text", true);
        ErrorResponse errorResponse = new ErrorResponse("Неполные данные для создания вещи",
                "недостаточно данных");

        when(service.addItem(1, itemDto))
                .thenThrow(new IncorrectItemDataExeption("недостаточно данных"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())))
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemDto = makeItemDto("Text", "Text text", true);
        User owner = makeUser(2, "Jony", "jony@dow.com");
        Item item = makeItem(1,"Text", "Text text", owner, true);

        when(service.updateItem(1, itemDto, 1))
                .thenReturn(item);

        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    void updateItemNotOwner() throws Exception {
        ItemDto itemDto = makeItemDto("Text", "Text text", true);
        ErrorResponse errorResponse = new ErrorResponse("Ошибка данных",
                "в доступе отказано, чужая вещь");

        when(service.updateItem(1, itemDto, 1))
                .thenThrow(new IncorrectItemOwnerExeption("в доступе отказано, чужая вещь"));

        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())))
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }


    @Test
    void getItems() throws Exception {
        List<ItemDtoWithBooking> items = new ArrayList<>();

        when(service.getAllItemsByUser(1, 0, 20))
                .thenReturn(items);

        mvc.perform(get("/items?from={from}&size={size}", "0", "20")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(items.size())));
    }

    @Test
    void getItemById() throws Exception {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setId(1);
        User owner = makeUser(1, "Jon", "jon@dow.com");
        User booker = makeUser(2, "Jony", "jony@dow.com");
        Item item = makeItem(1,"Text", "Text text", owner, true);
        Booking lastBooking = makeBooking(1, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2), item, booker, BookingStatus.APPROVED);
        Booking nextBooking = makeBooking(3, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3), item, booker, BookingStatus.APPROVED);
        itemDtoWithBooking.setNextBooking(toShortBookingDto(nextBooking));
        itemDtoWithBooking.setLastBooking(toShortBookingDto(lastBooking));

        when(service.getItemById(anyInt(), anyInt()))
                .thenReturn(itemDtoWithBooking);

        mvc.perform(get("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBooking.getId())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoWithBooking.getNextBooking().getId())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoWithBooking.getLastBooking().getId())));
    }

    @Test
    void getItemByIdNotFound() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка данных",
                "неверный id вещи");

        when(service.getItemById(anyInt(), anyInt()))
                .thenThrow(new IncorrectItemIdExeption("неверный id вещи"));

        mvc.perform(get("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorResponse.getError())))
                .andExpect(jsonPath("$.description", is(errorResponse.getDescription())));
    }

    @Test
    void searchItem() throws Exception {
        List<Item> items = new ArrayList<>();

        when(service.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}", "text", "0", "20")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(items.size())));
    }

    @Test
    void addCommentToItem() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        ShortCommentDto shortCommentDto = new ShortCommentDto();
        shortCommentDto.setId(1);

        when(service.addCommentToItem(1, 1, commentDto))
                .thenReturn(shortCommentDto);

        mvc.perform(post("/items/{itemId}/comment", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(shortCommentDto.getId())));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private Item makeItem(int id, String name, String description, User user, Boolean available) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setUser(user);
        item.setDescription(description);
        item.setAvailable(available);
        return item;
    }

    private User makeUser(int id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Booking makeBooking(int id, LocalDateTime start, LocalDateTime end, Item item, User booker,
                                BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }
}
