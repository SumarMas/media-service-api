package com.platform.media_service.controllers.manageExceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Custom exception class to handle exceptions with custom messages and HTTP
 * status codes.
 */
@Getter
public class CustomException extends RuntimeException {

    /**
     * This is the serialVersionUID used for serialization.
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Holds the HTTP status associated with this exception.
     */
    private final HttpStatus status;

    /**
     * Constructs a new CustomException with
     * the specified detail message and HTTP status.
     *
     * @param message     the detail message explaining the exception
     * @param statusParam the HTTP status code associated with this exception
     */
    public CustomException(String message, HttpStatus statusParam) {
        super(message);
        this.status = statusParam;
    }

    /**
     * Constructs a new CustomException.
     *
     * @param message     the detail message explaining the exception
     * @param statusParam the HTTP status code associated with this exception
     * @param causeParam  the original exception (cause) that triggered this exception
     */
    public CustomException(String message, HttpStatus statusParam, Throwable causeParam) {
        super(message, causeParam);
        this.status = statusParam;
    }
}
