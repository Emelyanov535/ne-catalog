package ru.necatalog.auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
	@NotBlank(message = "Username cannot be blank")
	@Email(message = "Username must be a valid email address")
	private String username;
	@NotBlank(message = "Password cannot be blank")
	private String password;
}
