package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingService service;

    @Test
    void addBooking() throws Exception {
        BookingDto bookingDto = makeBookingDto(1,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking booking = new Booking();
        booking.setId(1);

        when(service.addBooking(1, bookingDto))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())));
    }

    @Test
    void approveBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(1);

        when(service.approveBooking(1, 1,true))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", "1", "true")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())));
    }

    @Test
    void getBookingById() throws Exception {
        Booking booking = new Booking();
        booking.setId(1);

        when(service.getBookingById(anyInt(), anyInt()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", "1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())));
    }

    @Test
    void getBookingByState() throws Exception {
        List<Booking> bookings = new ArrayList<>();

        when(service.getBookingByState(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/?state={state}&from={from}&size={size}", "ALL", "0", "20")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bookings.size())));
    }

    @Test
    void getBookingByOwner() throws Exception {
        List<Booking> bookings = new ArrayList<>();

        when(service.getBookingByOwner(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "ALL", "0", "20")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bookings.size())));
    }

    private BookingDto makeBookingDto(int itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);
        return bookingDto;
    }
}
