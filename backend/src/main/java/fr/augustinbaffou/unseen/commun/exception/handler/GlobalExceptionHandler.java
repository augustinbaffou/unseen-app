package fr.augustinbaffou.unseen.commun.exception.handler;

import fr.augustinbaffou.unseen.commun.exception.ExceptionMessages;
import fr.augustinbaffou.unseen.commun.exception.ResourceAlreadyExistsException;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import fr.augustinbaffou.unseen.commun.exception.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                ExceptionMessages.HTTP_NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        return ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                ExceptionMessages.HTTP_CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ExceptionMessages.HTTP_BAD_REQUEST,
                ExceptionMessages.TYPE_MISMATCH.formatted(ex.getValue(), ex.getName()),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex, HttpServletRequest request) {
        return ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ExceptionMessages.HTTP_INTERNAL_SERVER_ERROR,
                ExceptionMessages.UNEXPECTED_ERROR,
                request.getRequestURI()
        );
    }
}
