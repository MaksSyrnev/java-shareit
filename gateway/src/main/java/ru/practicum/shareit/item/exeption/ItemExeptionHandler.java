package ru.practicum.shareit.item.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.ItemController;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice(assignableTypes = {ItemController.class})
public class ItemExeptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectUpdateItemData(final IllegalArgumentException e) {
        log.error("валидация данных: - {}", e.getMessage(), e);
        return new ErrorResponse(
                e.getMessage(), ""
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidItemDataExeption(final ValidationException e) {
        log.error("валидация данных: - {}", e.getMessage(), e);
        return new ErrorResponse(
                e.getMessage(), ""
        );
    }
}
