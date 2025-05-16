package ru.necatalog.auth.service;

import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.necatalog.auth.utils.jwt.JwtAuthentication;
import ru.necatalog.auth.utils.jwt.JwtProvider;
import ru.necatalog.auth.web.dto.JwtRequest;
import ru.necatalog.auth.web.dto.JwtResponse;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.repository.UserRepository;

@Service
@AllArgsConstructor
public class AuthService implements UserDetailsService {

	private final UserRepository userRepository;
	private final Map<String, String> refreshStorage = new HashMap<>();
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;

	@SneakyThrows
	public JwtResponse login(@NonNull JwtRequest authRequest) {
		final UserEntity user = userRepository.findByUsername(authRequest.getUsername())
				.orElseThrow(() -> new AuthException("Пользователь не существует"));
		if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
			final String accessToken = jwtProvider.generateAccessToken(user);
			final String refreshToken = jwtProvider.generateRefreshToken(user);
			refreshStorage.put(user.getUsername(), refreshToken);
			return new JwtResponse(accessToken, refreshToken);
		} else {
			throw new AuthException("Неверные учетные данные");
		}
	}

	@SneakyThrows
	public JwtResponse getAccessToken(@NonNull String refreshToken) {
		if (jwtProvider.validateRefreshToken(refreshToken)) {
			final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
			final String username = claims.getSubject();
			final String saveRefreshToken = refreshStorage.get(username);
			if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
				final UserEntity user = userRepository.findByUsername(username)
						.orElseThrow(() -> new AuthException("User not found"));
				final String accessToken = jwtProvider.generateAccessToken(user);
				return new JwtResponse(accessToken, null);
			}
		}
		return new JwtResponse(null, null);
	}

	@SneakyThrows
	public JwtResponse refresh(@NonNull String refreshToken) {
		if (jwtProvider.validateRefreshToken(refreshToken)) {
			final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
			final String username = claims.getSubject();
			final String saveRefreshToken = refreshStorage.get(username);
			if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
				final UserEntity user = userRepository.findByUsername(username)
						.orElseThrow(() -> new AuthException("User not found"));
				final String accessToken = jwtProvider.generateAccessToken(user);
				final String newRefreshToken = jwtProvider.generateRefreshToken(user);
				refreshStorage.put(user.getUsername(), newRefreshToken);
				return new JwtResponse(accessToken, newRefreshToken);
			}
		}
		throw new AuthException("Невалидный JWT токен");
	}

	public JwtAuthentication getAuthInfo() {
		return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return (UserDetails) userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	}
}
