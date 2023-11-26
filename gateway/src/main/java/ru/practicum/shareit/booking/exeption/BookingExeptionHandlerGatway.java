package ru.practicum.shareit.booking.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;

@Slf4j
@RestControllerAdvice(assignableTypes = {BookingController.class})
public class BookingExeptionHandlerGatway {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectBookingData(final IllegalArgumentException e) {
        log.error("валидация данных: - {}", e.getMessage(), e);
        return new ErrorResponse(
            e.getMessage(), ""
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValidBookingException(final MethodArgumentNotValidException e) {
        log.error("валидация данных: - {}", e.getMessage(), e);
        return new ErrorResponse(
                e.getMessage(), ""
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowableBookingException(final Throwable e) {
        log.error("валидация данных: - {}", e.getMessage(), e);
        return new ErrorResponse(
                e.getMessage(), ""
        );
    }
}
