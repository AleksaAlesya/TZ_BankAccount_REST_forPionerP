package by.aleksabrakor.bank_accounts.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST API.
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    /**
     * Обработка кастомных исключений "Не найдено"
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ErrorResponse handException(NotFoundException e) {
        log.error("Not found: ", e);
        return messageErrorResponse("Object was not found: ", e);
    }

    /**
     * Обработка ошибок при проверке на уникальность
     */
    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponse handException(AlreadyExistsException e) {
        log.error("Not create: ", e);
        return messageErrorResponse("Was not create: ", e);
    }

    /**
     * Обработка невалидных аргументов запроса
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponse handException(IllegalArgumentException e) {
        log.error("Invalid request argument: ", e);
        return messageErrorResponse("Invalid request argument: ", e);
    }

    /**
     * Обработка ошибок валидации DTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation failed: ", e);
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        return new ErrorResponse(
                "Validation failed: " + errorMessage,
                LocalDateTime.now()
        );
    }

    /**
     * Обработка нарушений целостности данных (уникальность, внешние ключи)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Error DB: ", e);
//        return messageErrorResponse("Error DB: ", e);
//        //для прода более подходящий вариант
        return new ErrorResponse("Error DB: ",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponse handException(IllegalStateException e) {
        log.error("Cannot delete: ", e);
        return messageErrorResponse("Cannot delete: ", e);
    }

    /**
     * Обработка ошибок доступа
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException e) {
        log.error("No access to resource: ", e);
        return messageErrorResponse("No access to resource: ", e);
    }

    /**
     * Универсальный обработчик всех непредвиденных исключений
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAll(Exception e) {
        log.error("Unexpected error: ", e);
        return messageErrorResponse("Unexpected error: ", e);
    }

    /**
     * Создание объекта ошибки из сообщения и исключения
     */
    private ErrorResponse messageErrorResponse(String message, Exception e) {
        return new ErrorResponse(
                message + e.getMessage(),
                LocalDateTime.now()
        );
    }
}
