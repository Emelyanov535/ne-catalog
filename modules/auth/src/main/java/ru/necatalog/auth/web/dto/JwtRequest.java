package ru.necatalog.auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtRequest {
	@NotBlank(message = "Username cannot be blank")
	@Email(message = "Username must be a valid email address")
	private String username;
	@NotBlank(message = "Password cannot be blank")
	private String password;
}
