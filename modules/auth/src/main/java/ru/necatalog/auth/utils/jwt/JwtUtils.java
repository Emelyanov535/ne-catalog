package ru.necatalog.auth.utils.jwt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.necatalog.persistence.enumeration.Role;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

	public static JwtAuthentication generate(Claims claims) {
		final JwtAuthentication jwtInfoToken = new JwtAuthentication();
		jwtInfoToken.setRoles(getRoles(claims));
		jwtInfoToken.setUsername(claims.getSubject());
		return jwtInfoToken;
	}

	private static Set<Role> getRoles(Claims claims) {
		final List<String> roles = claims.get("roles", List.class);
		return roles.stream()
				.map(Role::valueOf)
				.collect(Collectors.toSet());
	}
}
