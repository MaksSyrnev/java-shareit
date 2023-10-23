package ru.practicum.shareit.booking.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.exeption.ErrorResponse;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice(assignableTypes = {BookingController.class})
public class BookingExeptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectBookingData(final IncorrectBookingDataExeption e) {
        log.error("валидация данных: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Некоректные данные для бронирования вещи", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectItemIdOrUserIdBoking(final IncorrectItemIdOrUserIdBoking e) {
        log.error("валидация данных: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Не найден id", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementInBoking(final NoSuchElementException e) {
        log.error("валидация данных: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Не найден id", e.getMessage()
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ErrorResponse handleIncorrectStatusBooking(final IncorrectStatusBookingExeption e) {
        log.error("валидация данных: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Unknown state: " + e.getMessage(), e.getMessage()
        );
    }
}
