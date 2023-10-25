package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findAllByItemUserIdOrderByStartDesc(int ownerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int bookerId, BookingStatus state);

    List<Booking> findAllByBookerIdAndStatusNotOrderByStartDesc(int bookerId, BookingStatus state);

    List<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(int ownerId, BookingStatus state);

    List<Booking> findAllByItemUserIdAndStatusNotOrderByStartDesc(int ownerId, BookingStatus state);

    List<Booking> findAllByItemIdOrderByStartDesc(int itemId);

    List<Booking> findAllByItemIdAndBookerIdAndStatus(int itemId, int bokerId, BookingStatus state);

}
