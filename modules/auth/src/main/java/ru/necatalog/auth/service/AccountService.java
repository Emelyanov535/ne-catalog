package ru.necatalog.auth.service;

import java.util.Set;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.necatalog.auth.web.dto.RegisterDto;
import ru.necatalog.auth.web.dto.UserInfo;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.enumeration.Role;
import ru.necatalog.persistence.repository.UserRepository;

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
				.isNotification(true)
				.build();

		return userRepository.save(userEntity).getId();
	}

	public UserEntity getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

	public UserInfo getUserInfo() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserEntity userEntity = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return UserInfo.builder()
				.username(userEntity.getUsername())
				.isNotification(userEntity.getIsNotification())
				.build();
	}

	public void changeNotificationStatus() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserEntity userEntity = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		userEntity.setIsNotification(!userEntity.getIsNotification());
		userRepository.save(userEntity);
	}
}
