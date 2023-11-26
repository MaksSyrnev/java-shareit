package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findAllByUserIdOrderByIdAsc(int userId, Pageable page);

    @Query("select i from Item i " +
           "where upper(i.name) like upper(concat('%', ?1, '%')) " +
           "or upper(i.description) like upper(concat('%', ?1, '%'))")
    Page<Item> search(String text, Pageable page);

    List<Item> findByRequestId(int requestId);

    List<Item> findByRequestIdIn(Set<Integer> requsts);
}
