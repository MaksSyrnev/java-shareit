package ru.practicum.shareit.user.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.UserController;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice(assignableTypes ={UserController.class})
public class UserExeptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectUpdateUserData(final IllegalArgumentException e) {
        log.error("валидация данных: - {}", e.getMessage(), e);
        return new ErrorResponse(
                e.getMessage(), ""
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectCreateUserData(final ConstraintViolationException e) {
        log.error("валидация данных: - {}", e.getMessage(), e);
        return new ErrorResponse(
                e.getMessage(), ""
        );
    }
}
