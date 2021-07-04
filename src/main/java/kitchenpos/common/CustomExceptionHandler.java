package kitchenpos.common;

import kitchenpos.menu.exception.NotCreateMenuException;
import kitchenpos.table.exception.NotChangeEmptyException;
import kitchenpos.table.exception.NotChangeNumberOfGuestsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NotChangeEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleChangeEmptyException(NotChangeEmptyException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NotChangeNumberOfGuestsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotChangeNumberOfGuestsException(NotChangeNumberOfGuestsException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotFoundEntityException(NotFoundEntityException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NotCreateMenuException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotCreateMenuException(NotCreateMenuException e) {
        return e.getMessage();
    }
}
