package ru.necatalog.auth.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.necatalog.auth.utils.jwt.JwtProvider;
import ru.necatalog.auth.web.dto.JwtRequest;
import ru.necatalog.auth.web.dto.JwtResponse;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.repository.UserRepository;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Test
	void login_ShouldReturnTokens_WhenCredentialsValid() {
		JwtRequest request = new JwtRequest();
		request.setPassword("pass");
		request.setUsername("user");
		UserEntity user = new UserEntity();
		user.setUsername("user");
		user.setPassword("hashed");

		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
		when(jwtProvider.generateAccessToken(user)).thenReturn("access");
		when(jwtProvider.generateRefreshToken(user)).thenReturn("refresh");

		JwtResponse response = authService.login(request);

		assertEquals("access", response.getAccessToken());
		assertEquals("refresh", response.getRefreshToken());
	}

	@Test
	void getAccessToken_ShouldReturnAccessToken_WhenRefreshValid() throws NoSuchFieldException, IllegalAccessException {
		String token = "refresh";
		Claims claims = mock(Claims.class);
		when(jwtProvider.validateRefreshToken(token)).thenReturn(true);
		when(jwtProvider.getRefreshClaims(token)).thenReturn(claims);
		when(claims.getSubject()).thenReturn("user");

		UserEntity user = new UserEntity();
		user.setUsername("user");

		Field field = AuthService.class.getDeclaredField("refreshStorage");
		field.setAccessible(true);
		Map<String, String> map = (Map<String, String>) field.get(authService);
		map.put("user", "refresh");

		when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
		when(jwtProvider.generateAccessToken(user)).thenReturn("access");

		JwtResponse response = authService.getAccessToken(token);

		assertEquals("access", response.getAccessToken());
	}
}

