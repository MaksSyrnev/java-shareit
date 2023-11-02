package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exeption.IncorrectDataItemRequestExeption;
import ru.practicum.shareit.request.exeption.IncorrectIdRequestExeption;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestReopository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.domain.PageRequest.of;

public class ItemReqestServiceImplTest {
    private final ItemRequestReopository mockReopository = Mockito.mock(ItemRequestReopository.class);
    private final UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    private final ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);

    final ItemRequestServiceImpl requestService = new ItemRequestServiceImpl(mockReopository,
            mockUserRepository, mockItemRepository);

    @Test
    @DisplayName("AddNewItemRequest - вызов метода реопозитория")
    void testAddNewItemRequest() {
        User user = makeUser(1, "Jon", "jon@dow.com");
        ItemRequestDto itemRequestDto = makeItemRequestDto("text", user, LocalDateTime.now());

        Mockito
                .when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        requestService.addNewItemRequest(1, itemRequestDto);

        Mockito.verify(mockReopository, Mockito.times(1))
                .save(Mockito.any());
    }

    @Test
    @DisplayName("GetRequestById - вызовы правильных методов реопозиториеев")
    void testGetRequestById() {
        User user = makeUser(1, "Jon", "jon@dow.com");
        ItemRequest request = makeItemRequest(1, "text", user, LocalDateTime.now());
        List<Item> items = new ArrayList<>();

        Mockito
                .when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(mockReopository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(mockItemRepository.findByRequestId(Mockito.anyInt()))
                .thenReturn(items);

        requestService.getRequestById(1, 1);

        Mockito.verify(mockReopository, Mockito.times(1))
                .findById(Mockito.anyInt());

        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findByRequestId(Mockito.anyInt());
    }

    @Test
    @DisplayName("GetRequestById - неверный айди запроса")
    void testGetRequestByIdNotFound() {
        User user = makeUser(1, "Jon", "jon@dow.com");

        Mockito
                .when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        final IncorrectIdRequestExeption ex = assertThrows(
                IncorrectIdRequestExeption.class,
                () -> requestService.getRequestById(1, 1)
        );

        Assertions.assertEquals(ex.getMessage(), "Запрос с таким id не найден",
                "Ошибка не верная или не произошла");
    }

    @Test
    @DisplayName("GetAllUserRequest - вызовы правильных методов реопозиториев")
    void testGetAllUserRequest() {
        User user = makeUser(1, "Jon", "jon@dow.com");
        ItemRequest request = makeItemRequest(1, "text", user, LocalDateTime.now());
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request);
        List<Item> items = new ArrayList<>();

        Mockito
                .when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(mockReopository.findAllByRequestorId(Mockito.anyInt()))
                .thenReturn(requests);

        Mockito
                .when(mockItemRepository.findByRequestIdIn(Mockito.anySet()))
                .thenReturn(items);

        requestService.getAllUserRequest(1);

        Mockito.verify(mockReopository, Mockito.times(1))
                .findAllByRequestorId(Mockito.anyInt());

        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findByRequestIdIn(Mockito.anySet());
    }

    @Test
    @DisplayName("GetAllRequest - вызовы правильных методов реопозиториев")
    void testGetAllRequest() {
        User user = makeUser(1, "Jon", "jon@dow.com");
        User userJoe = makeUser(2, "Jon", "jon@dow.com");
        ItemRequest request = makeItemRequest(1, "text", userJoe, LocalDateTime.now());
        PageRequest page = of(0, 10);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request);
        Page<ItemRequest> pagedResponse = new PageImpl(requests);
        List<Item> items = new ArrayList<>();

        Mockito
                .when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(mockReopository.findAllByRequestorIdNot(1, page))
                .thenReturn(pagedResponse);

        Mockito
                .when(mockItemRepository.findByRequestIdIn(Mockito.anySet()))
                .thenReturn(items);

        requestService.getAllRequest(1, 0, 10);

        Mockito.verify(mockReopository, Mockito.times(1))
                .findAllByRequestorIdNot(1, page);

        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findByRequestIdIn(Mockito.anySet());
    }

    private ItemRequest makeItemRequest(int id, String description, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        return itemRequest;
    }

    private ItemRequestDto makeItemRequestDto(String description, User requestor, LocalDateTime created) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("text");
        itemRequestDto.setRequestor(requestor);
        return itemRequestDto;
    }

    private User makeUser(int id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item makeItem(int id, String name, String description, User user, Boolean available,
                          ItemRequest request) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setUser(user);
        item.setDescription(description);
        item.setAvailable(available);
        item.setRequest(request);
        return item;
    }
}
