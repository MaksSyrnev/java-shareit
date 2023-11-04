package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.exeption.IncorrectIdRequestExeption;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemRequestService requestService;

    @Test
    void addNewRequest() throws Exception {
        User user = makeUser(1, "Mi6a", "mi6a@mail.ru");
        ItemRequestDto itemRequestDto = makeItemRequestDto("text", user, LocalDateTime.now());
        ItemRequest itemRequest = makeItemRequest(1,"text", user, LocalDateTime.now());

        when(requestService.addNewItemRequest(1, itemRequestDto))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId())));
    }

    @Test
    void getAllUserRequest() throws Exception {
        List<ItemRequestWithItemsDto> requests = new ArrayList<>();

        when(requestService.getAllUserRequest(anyInt()))
                .thenReturn(requests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(requests.size())));
    }

    @Test
    void getAllRequest() throws Exception {
        List<ItemRequestWithItemsDto> requests = new ArrayList<>();

        when(requestService.getAllRequest(anyInt(), anyInt(), anyInt()))
                .thenReturn(requests);

        mvc.perform(get("/requests/all?from={from}&size={size}", "0", "20")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(requests.size())));
    }

    @Test
    void getRequestById() throws Exception {
        ItemRequestWithItemsDto request = new ItemRequestWithItemsDto();
        request.setId(1);

        when(requestService.getRequestById(anyInt(), anyInt()))
                .thenReturn(request);

        mvc.perform(get("/requests/{id}", "0")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId())));
    }

    @Test
    void getRequestByIdNotFound() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка поиска запроса",
                "Запрос с таким id не найден");

        when(requestService.getRequestById(anyInt(), anyInt()))
                .thenThrow(new IncorrectIdRequestExeption("Запрос с таким id не найден"));

        mvc.perform(get("/requests/{id}", "0")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect((ResultMatcher) jsonPath("$.error", is(errorResponse.getError())))
                .andExpect((ResultMatcher) jsonPath("$.description", is(errorResponse.getDescription())));
    }

    private ItemRequestDto makeItemRequestDto(String description, User requestor, LocalDateTime created) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("text");
        itemRequestDto.setRequestor(requestor);
        return itemRequestDto;
    }

    private ItemRequest makeItemRequest(int id, String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        return itemRequest;
    }

    private User makeUser(int id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
