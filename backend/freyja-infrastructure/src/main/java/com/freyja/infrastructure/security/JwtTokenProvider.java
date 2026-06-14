package com.freyja.infrastructure.security;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.freyja.domain.model.user.User;
import com.freyja.domain.port.out.TokenProvider;
import com.freyja.domain.vo.IssuedToken;
import com.freyja.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {

  private static final String CLAIM_EMAIL = "email";

  private static final String CLAIM_ROLE = "role";

  private final JwtProperties properties;

  private final SecretKey key;

  public JwtTokenProvider(JwtProperties properties) {
    this.properties = properties;
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret()));
  }

  @Override
  public IssuedToken issue(User user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusMillis(properties.getExpirationMs());
    String token = Jwts.builder()
        .subject(user.id().toString())
        .issuer(properties.getIssuer())
        .claim(CLAIM_EMAIL, user.email().value())
        .claim(CLAIM_ROLE, user.role().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(key)
        .compact();
    return new IssuedToken(token, expiresAt);
  }

  /**
   * Verify a token and extract the authenticated principal.
   *
   * @throws io.jsonwebtoken.JwtException if the token is invalid or expired.
   */
  public AuthenticatedUser parse(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(key)
        .requireIssuer(properties.getIssuer())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return new AuthenticatedUser(
        UUID.fromString(claims.getSubject()),
        claims.get(CLAIM_EMAIL, String.class),
        claims.get(CLAIM_ROLE, String.class));
  }
}
