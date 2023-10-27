package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findAllByBookerIdOrderByStartDesc(int bookerId, Pageable page);

    Page<Booking> findAllByItemUserIdOrderByStartDesc(int ownerId, Pageable page);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int bookerId, BookingStatus state, Pageable page);

    Page<Booking> findAllByBookerIdAndStatusNotOrderByStartDesc(int bookerId, BookingStatus state, Pageable page);

    Page<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(int ownerId, BookingStatus state, Pageable page);

    Page<Booking> findAllByItemUserIdAndStatusNotOrderByStartDesc(int ownerId, BookingStatus state, Pageable page);

    List<Booking> findAllByItemIdOrderByStartDesc(int itemId);

    List<Booking> findAllByItemIdAndBookerIdAndStatus(int itemId, int bokerId, BookingStatus state);

}
