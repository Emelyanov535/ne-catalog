package ru.necatalog.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.necatalog.auth.web.dto.RegisterDto;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.enumeration.Role;
import ru.necatalog.persistence.repository.UserRepository;

import java.util.Set;

@Service
@AllArgsConstructor
public class AccountService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public Long register(RegisterDto registerDto) {
		UserEntity userEntity = UserEntity.builder()
				.username(registerDto.getUsername())
				.password(passwordEncoder.encode(registerDto.getPassword()))
				.roles(Set.of(Role.USER))
				.build();

		return userRepository.save(userEntity).getId();
	}

	public UserEntity getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}
