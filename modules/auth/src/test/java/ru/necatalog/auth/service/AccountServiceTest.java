package ru.necatalog.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.necatalog.auth.web.dto.RegisterDto;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AccountService accountService;

	@Test
	void register_ShouldSaveUser() {
		RegisterDto dto = new RegisterDto("testuser", "pass");
		UserEntity savedUser = UserEntity.builder().id(1L).build();

		when(passwordEncoder.encode("pass")).thenReturn("hashed");
		when(userRepository.save(any())).thenReturn(savedUser);

		Long result = accountService.register(dto);

		assertEquals(1L, result);
		verify(userRepository).save(any(UserEntity.class));
	}

	@Test
	void getCurrentUser_ShouldReturnUser() {
		String username = "testuser";
		UserEntity user = new UserEntity();
		user.setUsername(username);

		var auth = new TestingAuthenticationToken(username, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

		UserEntity result = accountService.getCurrentUser();
		assertEquals(username, result.getUsername());
	}
}

