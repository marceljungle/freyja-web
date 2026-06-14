package com.freyja.application.user;

import com.freyja.application.common.AbstractReadOnlyUseCase;
import com.freyja.domain.exception.AuthenticationException;
import com.freyja.domain.model.user.User;
import com.freyja.domain.port.out.PasswordHasher;
import com.freyja.domain.port.out.TokenProvider;
import com.freyja.domain.port.out.UserRepository;
import com.freyja.domain.vo.Email;
import com.freyja.domain.vo.IssuedToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserUseCase
    extends AbstractReadOnlyUseCase<AuthenticateUserCommand, AuthenticationResult> {

  private final UserRepository userRepository;

  private final PasswordHasher passwordHasher;

  private final TokenProvider tokenProvider;

  public AuthenticateUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
      TokenProvider tokenProvider) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.tokenProvider = tokenProvider;
  }

  @Override
  protected AuthenticationResult handle(AuthenticateUserCommand input) {
    // Generic failure message: never reveal whether the email exists.
    User user = userRepository.findByEmail(Email.of(input.email()))
        .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

    if (!passwordHasher.matches(input.password(), user.passwordHash())) {
      throw new AuthenticationException("Invalid email or password");
    }

    IssuedToken token = tokenProvider.issue(user);
    return AuthenticationResult.of(user, token);
  }
}
