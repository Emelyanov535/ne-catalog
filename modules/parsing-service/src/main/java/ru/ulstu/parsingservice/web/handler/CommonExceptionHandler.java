package ru.ulstu.parsingservice.web.handler;

import java.net.URI;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex,
                                                          HttpServletRequest request) {
        return handleException(HttpStatus.BAD_REQUEST, request, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex,
                                                                 HttpServletRequest request) {
        return handleException(HttpStatus.NOT_FOUND, request, ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(Exception ex,
                                                                            HttpServletRequest request) {
        return handleException(HttpStatus.INTERNAL_SERVER_ERROR, request, ex);
    }

    /*@ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleForbiddenException(Exception ex,
                                                                  HttpServletRequest request) {
        return handleException(HttpStatus.FORBIDDEN, request, ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Exception ex,
                                                                     HttpServletRequest request) {
        return handleException(HttpStatus.UNAUTHORIZED, request, ex);
    }*/

    private ResponseEntity<ErrorResponse> handleException(HttpStatus status, HttpServletRequest request, Exception ex) {
        var errorResponse = new ErrorResponse(
            status.value(),
            status,
            URI.create(request.getRequestURI()),
            ex.getMessage()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
