package com.freyja.application.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import com.freyja.domain.exception.ConflictException;
import com.freyja.domain.exception.ValidationException;
import com.freyja.domain.model.user.User;
import com.freyja.domain.port.out.PasswordHasher;
import com.freyja.domain.port.out.TimeProvider;
import com.freyja.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

  @Mock
  UserRepository userRepository;

  @Mock
  PasswordHasher passwordHasher;

  @Mock
  TimeProvider time;

  @InjectMocks
  RegisterUserUseCase useCase;

  @Test
  void registersAndHashesPassword() {
    when(time.now()).thenReturn(Instant.parse("2026-01-01T00:00:00Z"));
    when(userRepository.existsByEmail(any())).thenReturn(false);
    when(passwordHasher.hash("supersecret")).thenReturn("HASH");
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    RegisterUserResult result =
        useCase.execute(new RegisterUserCommand("Demo@Freyja.io", "supersecret", "Demo"));

    assertEquals("demo@freyja.io", result.email()); // normalised
    verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(
        (User u) -> "HASH".equals(u.passwordHash())));
  }

  @Test
  void rejectsDuplicateEmail() {
    when(userRepository.existsByEmail(any())).thenReturn(true);
    assertThrows(ConflictException.class,
        () -> useCase.execute(new RegisterUserCommand("a@b.co", "supersecret", null)));
    verify(userRepository, never()).save(any());
  }

  @Test
  void validatesInputBeforeLogic() {
    // Too-short password fails validation before any port is touched.
    assertThrows(ValidationException.class,
        () -> useCase.execute(new RegisterUserCommand("a@b.co", "short", null)));
    verify(userRepository, never()).existsByEmail(any());
  }
}
