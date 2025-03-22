package ru.necatalog.auth.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.auth.service.AuthService;
import ru.necatalog.auth.web.dto.JwtRequest;
import ru.necatalog.auth.web.dto.JwtResponse;
import ru.necatalog.auth.web.dto.RefreshJwtRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Аутентификация и управление токенами")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "Авторизация пользователя и получение токенов")
	public ResponseEntity<JwtResponse> login(
			@RequestBody @Validated JwtRequest authRequest) {
		return ResponseEntity.ok(authService.login(authRequest));
	}

	@PostMapping("/token")
	@Operation(summary = "Обновление токена доступа по refresh токену")
	public ResponseEntity<JwtResponse> getNewAccessToken(
			@RequestBody @Validated RefreshJwtRequest request) {
		return ResponseEntity.ok(authService.getAccessToken(request.getRefreshToken()));
	}

	@PostMapping("/refresh")
	@Operation(summary = "Обновление refresh токена по существующему refresh токену", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<JwtResponse> getNewRefreshToken(
			@RequestBody @Validated RefreshJwtRequest request) {
		return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
	}
}
