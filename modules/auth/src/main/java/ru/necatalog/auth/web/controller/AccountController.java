package ru.necatalog.auth.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.auth.service.AccountService;
import ru.necatalog.auth.web.dto.RegisterDto;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Аккаунт", description = "Управление аккаунтами пользователей")
public class AccountController {

	private final AccountService accountService;

	@PostMapping("/register")
	@Operation(summary = "Регистрация пользователя")
	public ResponseEntity<?> register(
			@RequestBody @Validated RegisterDto registerDto) {
		return ResponseEntity.ok(accountService.register(registerDto));
	}

	@GetMapping("/whoami")
	@Operation(summary = "Получение информации о текущем пользователе", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<String> whoami() {
		return ResponseEntity.ok(accountService.getCurrentUser().getUsername());
	}
}
