package ru.necatalog.auth.utils.jwt;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.necatalog.auth.configuration.properties.JwtConfigProperties;
import ru.necatalog.persistence.entity.UserEntity;

@Slf4j
@Component
public class JwtProvider {

	private final SecretKey jwtAccessSecret;
	private final SecretKey jwtRefreshSecret;

	public JwtProvider(JwtConfigProperties jwtConfigProperties) {
		this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfigProperties.getAccess()));
		this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfigProperties.getRefresh()));
	}

	public String generateAccessToken(@NonNull UserEntity user) {
		final LocalDateTime now = LocalDateTime.now();
		final Instant accessExpirationInstant = now.plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant();
		final Date accessExpiration = Date.from(accessExpirationInstant);
		return Jwts.builder()
				.setSubject(user.getUsername())
				.setExpiration(accessExpiration)
				.signWith(jwtAccessSecret)
				.claim("roles", user.getRoles())
				.compact();
	}

	public String generateRefreshToken(@NonNull UserEntity user) {
		final LocalDateTime now = LocalDateTime.now();
		final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
		final Date refreshExpiration = Date.from(refreshExpirationInstant);
		return Jwts.builder()
				.setSubject(user.getUsername())
				.setExpiration(refreshExpiration)
				.signWith(jwtRefreshSecret)
				.compact();
	}

	public boolean validateAccessToken(@NonNull String accessToken) {
		return validateToken(accessToken, jwtAccessSecret);
	}

	public boolean validateRefreshToken(@NonNull String refreshToken) {
		return validateToken(refreshToken, jwtRefreshSecret);
	}

	private boolean validateToken(@NonNull String token, @NonNull Key secret) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(secret)
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException expEx) {
			log.error("Token expired", expEx);
		} catch (UnsupportedJwtException unsEx) {
			log.error("Unsupported jwt", unsEx);
		} catch (MalformedJwtException mjEx) {
			log.error("Malformed jwt", mjEx);
		} catch (SignatureException sEx) {
			log.error("Invalid signature", sEx);
		} catch (Exception e) {
			log.error("invalid token", e);
		}
		return false;
	}

	public Claims getAccessClaims(@NonNull String token) {
		return getClaims(token, jwtAccessSecret);
	}

	public Claims getRefreshClaims(@NonNull String token) {
		return getClaims(token, jwtRefreshSecret);
	}

	private Claims getClaims(@NonNull String token, @NonNull Key secret) {
		return Jwts.parserBuilder()
				.setSigningKey(secret)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}
