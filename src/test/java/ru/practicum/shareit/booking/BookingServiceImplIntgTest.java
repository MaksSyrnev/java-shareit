package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@Transactional
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntgTest {
    private final EntityManager em;
    private final BookingServiceImpl service;

    @Test
    @DisplayName("getBookingByState - интеграция")
    void getBookingByState() {
        NewUserDto userOne = makeNewUserDto("Миша", "mi6a@mail.ru");
        NewUserDto userTwo = makeNewUserDto("Маша", "ma6a@mail.ru");
        User owner = UserMapper.toUser(userOne);
        User booker = UserMapper.toUser(userTwo);
        em.persist(owner);
        em.persist(booker);
        log.info(" user - {}", owner);
        log.info(" user - {}", booker);

        Item itemOne = makeItem("One", "text", owner, Boolean.TRUE);
        em.persist(itemOne);

        List<Booking> bokings = List.of(
                makeBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                    itemOne, booker, BookingStatus.WAITING),
                makeBooking(LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5),
                    itemOne, booker, BookingStatus.APPROVED)
        );

        for (Booking boking : bokings) {
            em.persist(boking);
        }
        em.flush();

        List<Booking> targetBookings = service.getBookingByState(booker.getId(), "FUTURE", 0, 10);

        assertThat(targetBookings, hasSize(bokings.size()));
        for (Booking boking : bokings) {
            assertThat(targetBookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(boking.getStart())),
                    hasProperty("item", equalTo(boking.getItem()))
            )));
        }

        List<Booking> targetCurrentBookings = service.getBookingByState(booker.getId(), "CURRENT", 0, 10);
        assertThat(targetCurrentBookings, hasSize(0));

        List<Booking> targetPastBookings = service.getBookingByState(booker.getId(), "PAST", 0, 10);
        assertThat(targetPastBookings, hasSize(0));

        List<Booking> targetWaitingBookings = service.getBookingByState(booker.getId(), "WAITING", 0, 10);
        assertThat(targetWaitingBookings, hasSize(1));

        List<Booking> targetRejBookings = service.getBookingByState(booker.getId(), "REJECTED", 0, 10);
        assertThat(targetRejBookings, hasSize(0));
    }


    private NewUserDto makeNewUserDto(String name, String email) {
        NewUserDto newUser = new NewUserDto();
        newUser.setName(name);
        newUser.setEmail(email);
        return newUser;
    }

    private Item makeItem(String name, String description, User user, Boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setUser(user);
        item.setDescription(description);
        item.setAvailable(available);
        return item;
    }

    private Booking makeBooking(LocalDateTime start, LocalDateTime end, Item item, User booker,
                                BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }
}
