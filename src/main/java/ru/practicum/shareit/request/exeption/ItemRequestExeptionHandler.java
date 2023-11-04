package ru.practicum.shareit.request.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.request.controller.ItemRequestController;

@Slf4j
@RestControllerAdvice(assignableTypes = {ItemRequestController.class})
public class ItemRequestExeptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectDataItemRequest(final IncorrectDataItemRequestExeption e) {
        log.error("валидация данных: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Запрос не содержит описания", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectIdItemRequest(final IncorrectIdRequestExeption e) {
        log.error("валидация данных: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Ошибка поиска запроса", e.getMessage()
        );
    }
}
