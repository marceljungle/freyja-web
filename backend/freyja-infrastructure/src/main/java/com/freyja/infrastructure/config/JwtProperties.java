package com.freyja.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "freyja.security.jwt")
public class JwtProperties {

  /**
   * Base64-encoded signing secret (>= 256 bits for HS256).
   */
  private String secret;

  /**
   * Token issuer claim.
   */
  private String issuer = "freyja";

  /**
   * Token lifetime in milliseconds.
   */
  private long expirationMs = 86_400_000L;

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public long getExpirationMs() {
    return expirationMs;
  }

  public void setExpirationMs(long expirationMs) {
    this.expirationMs = expirationMs;
  }
}
