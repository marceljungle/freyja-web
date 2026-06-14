package com.freyja.application.user;

import com.freyja.application.common.AbstractUseCase;
import com.freyja.domain.exception.ConflictException;
import com.freyja.domain.model.user.User;
import com.freyja.domain.port.out.PasswordHasher;
import com.freyja.domain.port.out.TimeProvider;
import com.freyja.domain.port.out.UserRepository;
import com.freyja.domain.vo.Email;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCase extends AbstractUseCase<RegisterUserCommand, RegisterUserResult> {

  private final UserRepository userRepository;

  private final PasswordHasher passwordHasher;

  private final TimeProvider time;

  public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
      TimeProvider time) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.time = time;
  }

  @Override
  protected RegisterUserResult handle(RegisterUserCommand input) {
    Email email = Email.of(input.email());
    if (userRepository.existsByEmail(email)) {
      throw new ConflictException("An account with this email already exists");
    }
    String hash = passwordHasher.hash(input.password());
    User user = User.register(email, hash, input.displayName(), time.now());
    return RegisterUserResult.from(userRepository.save(user));
  }
}
