package com.platform.media_service.controllers.manageExceptions;

import com.platform.media_service.dtos.common.ErrorApi;
import lombok.NoArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller advice class to handle exceptions thrown by controllers.
 */
@ControllerAdvice
@NoArgsConstructor
public class ControllerExceptionHandler {
    /**
     * Handles CustomException and returns an ErrorApi response.
     *
     * @param e the CustomException thrown
     * @return a ResponseEntity with ErrorApi and HTTP status
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorApi> handleCustomException(CustomException e) {
        ErrorApi error = buildError(e.getMessage(), e.getStatus());
        return ResponseEntity.status(e.getStatus()).body(error);
    }

    /**
     * Handles general exceptions (Exception.class).
     * Builds and returns an ErrorApi response with the
     * exception message and a 500 Internal Server Error status.
     *
     * @param e the Exception thrown
     * @return a ResponseEntity containing the ErrorApi and a 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApi> handleError(Exception e) {
        ErrorApi error = buildError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles validation exceptions (MethodArgumentNotValidException)
     * and returns a 400 Bad Request response.
     *
     * @param e the exception thrown
     * @return a ResponseEntity with ErrorApi and a 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (msg1, msg2) -> msg1 // si hay duplicados, tomar el primero
                ));
        String message = fieldErrors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        ErrorApi error = buildError(message, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Helper method to build an ErrorApi object with
     * the provided message and status.
     *
     * @param message the error message
     * @param status  the HTTP status
     * @return an ErrorApi object containing the error details
     */
    private ErrorApi buildError(String message, HttpStatus status) {
        System.out.println("Error: " + message);
        return ErrorApi.builder()
                .timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(status.getReasonPhrase())
                .status(status.value())
                .message(message)
                .build();
    }
}
