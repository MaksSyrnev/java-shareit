package ru.practicum.shareit.user.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.user.controller.UserController;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class})
public class UserExeptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIncorrectEmailUserError(final IncorrectUserEmail e) {
        log.error("неверная почта: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectUserIdError(final IncorrectUserIdException e) {
        log.error("неверный id: - '{}'", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }
}
