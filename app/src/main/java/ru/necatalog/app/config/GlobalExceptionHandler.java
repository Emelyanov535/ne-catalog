package ru.necatalog.app.config;

import java.util.stream.Collectors;

import jakarta.security.auth.message.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.necatalog.auth.exception.EmailAlreadyUsedException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthException.class)
	public ResponseEntity<ExceptionResponse> handleAuthException(AuthException ex){
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
	}

	@ExceptionHandler(EmailAlreadyUsedException.class)
	public ResponseEntity<ExceptionResponse> handleEmailAlreadyUsedException(EmailAlreadyUsedException ex){
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex){
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), System.currentTimeMillis());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
		String errorMessages = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.collect(Collectors.joining(", "));

		ExceptionResponse exceptionResponse = new ExceptionResponse(
				HttpStatus.BAD_REQUEST.value(),
				errorMessages,
				System.currentTimeMillis()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleGeneralException(Exception ex) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"An unexpected error occurred: " + ex.getMessage(),
				System.currentTimeMillis()
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
	}
}
