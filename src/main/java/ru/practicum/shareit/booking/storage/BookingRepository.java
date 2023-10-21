package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId);
    List<Booking> findAllByItemUserIdOrderByStartDesc(int ownerId);
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int bookerId, BookingDtoState state);
    List<Booking> findAllByBookerIdAndStatusNotOrderByStartDesc(int bookerId, BookingDtoState state);
    List<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(int ownerId, BookingDtoState state);
    List<Booking> findAllByItemUserIdAndStatusNotOrderByStartDesc(int ownerId, BookingDtoState state);
    List<Booking> findAllByItemIdOrderByStartDesc(int itemId);
    List<Booking> findAllByItemIdAndBookerIdAndStatus(int itemId, int bokerId, BookingDtoState state);

}
