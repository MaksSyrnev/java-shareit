package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.List;

public interface ItemRequestReopository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByRequestorId(int userId);

    Page<ItemRequest> findAllByRequestorIdNot(int requestorId, Pageable page);

}
