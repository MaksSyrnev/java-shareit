package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exeption.IncorrectIdRequestExeption;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestReopository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.springframework.data.domain.PageRequest.*;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestReopository reopository;
    private final UserService userService;

    public ItemRequestServiceImpl(ItemRequestReopository reopository, UserService userService) {
        this.reopository = reopository;
        this.userService = userService;
    }

    @Override
    public ItemRequest addNewItemRequest(int userId, ItemRequestDto itemRequestDto) {
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription(itemRequestDto.getDescription());
        return reopository.save(itemRequest);
    }

    @Override
    public ItemRequest getRequestById(int requestId) {
        ItemRequest iRequest = reopository.findById(requestId).orElseThrow(
                () -> new IncorrectIdRequestExeption("Запрос с таким id не найден"));
        return iRequest;
    }

    @Override
    public List<ItemRequest> getAllUserRequest(int userId) {
        User user = userService.getUserById(userId);
        return reopository.findAllByRequestorId(userId);
    }

    @Override
    public List<ItemRequest> getAllRequest(int from, int size) {
        PageRequest page = of(from > 0 ? from / size : 0, size);
        return reopository.findAll(page).getContent();
//        return itemNoteRepository.findAllByItemUserId(userId, page)
//                .map(ItemNoteMapper::mapToItemNoteDto)
//                .getContent();
    }
}
