package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.exeption.IncorrectIdRequestExeption;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestReopository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.domain.PageRequest.of;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.makeRequestWithItemsDto;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestReopository reopository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestReopository reopository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.reopository = reopository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequest addNewItemRequest(int userId, ItemRequestDto itemRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IncorrectIdRequestExeption("Неверный id пользователя");
        }
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user.get());
        return reopository.save(itemRequest);
    }

    @Override
    public ItemRequestWithItemsDto getRequestById(int userId, int requestId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IncorrectIdRequestExeption("Неверный id пользователя");
        }
        Optional<ItemRequest> iRequest = reopository.findById(requestId);
        if (iRequest.isEmpty()) {
            throw new IncorrectIdRequestExeption("Запрос с таким id не найден");
        }
        final List<Item> findItems = itemRepository.findByRequestId(requestId);
        if (findItems.isEmpty()) {
            return makeRequestWithItemsDto(iRequest.get(), Collections.emptyList());
        }
        List<ItemDto> items = findItems.stream()
                .map(item -> toItemDto(item))
                .collect(Collectors.toList());

        return makeRequestWithItemsDto(iRequest.get(), items);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllUserRequest(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IncorrectIdRequestExeption("Неверный id пользователя");
        }
        Map<Integer, ItemRequest> requestMap = reopository.findAllByRequestorId(userId)
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        Map<Integer, List<ItemDto>> itemsMap = itemRepository.findByRequestIdIn(requestMap.keySet())
                .stream()
                .map(item -> toItemDto(item))
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
        List<ItemRequestWithItemsDto> findRequest = requestMap.values()
            .stream()
            .map(request -> makeRequestWithItemsDto(
                        request,
                        itemsMap.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
        return findRequest;
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllRequest(int userId, int from, int size) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IncorrectIdRequestExeption("Неверный id пользователя");
        }
        PageRequest page = of(from > 0 ? from / size : 0, size);
        final Map<Integer, ItemRequest> requestMap = reopository.findAllByRequestorIdNot(userId, page).getContent()
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        final Map<Integer, List<ItemDto>> itemsMap = itemRepository.findByRequestIdIn(requestMap.keySet())
                .stream()
                .map(item -> toItemDto(item))
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
        final List<ItemRequestWithItemsDto> finedRequests = requestMap.values()
                .stream()
                .map(request -> makeRequestWithItemsDto(request,
                        itemsMap.getOrDefault(request.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
        return finedRequests;
    }
}
