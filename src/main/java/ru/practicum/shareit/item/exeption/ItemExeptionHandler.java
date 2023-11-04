package ru.practicum.shareit.item.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.item.controller.ItemController;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice(assignableTypes = {ItemController.class})
public class ItemExeptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(final IncorrectItemDataExeption e) {
        log.error("валидация данных: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Неполные данные для создания вещи", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectItemIdError(final IncorrectItemIdExeption e) {
        log.error("неверный id: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIncorrectItemOwnerError(final IncorrectItemOwnerExeption e) {
        log.error("неверный id владельца: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ErrorResponse handleIncorrectDataComment(final IncorrectDataCommentExeption e) {
        log.error("валидация данных : - '{}'", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValidateDataRequestExeption(final ConstraintViolationException e) {
        log.error("валидация данных: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Ошибка: ", e.getMessage()
        );
    }
}
