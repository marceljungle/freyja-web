package com.freyja.domain.model.user;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.freyja.domain.exception.ValidationException;
import com.freyja.domain.vo.Email;

public class User {

  private final UUID id;

  private final Email email;

  private final Instant createdAt;

  private String passwordHash;

  private String displayName;

  private UserRole role;

  private Instant updatedAt;

  public User(UUID id, Email email, String passwordHash, String displayName,
      UserRole role, Instant createdAt, Instant updatedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.email = Objects.requireNonNull(email, "email");
    this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
    this.displayName = displayName;
    this.role = Objects.requireNonNull(role, "role");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  /**
   * Factory for a brand-new user (role USER).
   */
  public static User register(Email email, String passwordHash, String displayName, Instant now) {
    if (passwordHash == null || passwordHash.isBlank()) {
      throw new ValidationException("Password hash must not be blank");
    }
    return new User(UUID.randomUUID(), email, passwordHash,
        displayName, UserRole.USER, now, now);
  }

  public void changeDisplayName(String displayName, Instant now) {
    this.displayName = displayName;
    this.updatedAt = now;
  }

  public UUID id() {
    return id;
  }

  public Email email() {
    return email;
  }

  public String passwordHash() {
    return passwordHash;
  }

  public String displayName() {
    return displayName;
  }

  public UserRole role() {
    return role;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User other)) {
      return false;
    }
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
