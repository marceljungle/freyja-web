package com.freyja.infrastructure.rest;

import com.freyja.application.user.AuthenticateUserCommand;
import com.freyja.application.user.AuthenticateUserUseCase;
import com.freyja.application.user.RegisterUserCommand;
import com.freyja.application.user.RegisterUserResult;
import com.freyja.application.user.RegisterUserUseCase;
import com.freyja.infrastructure.rest.dto.AuthResponse;
import com.freyja.infrastructure.rest.dto.LoginRequest;
import com.freyja.infrastructure.rest.dto.RegisterUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final RegisterUserUseCase registerUser;

  private final AuthenticateUserUseCase authenticateUser;

  public AuthController(RegisterUserUseCase registerUser, AuthenticateUserUseCase authenticateUser) {
    this.registerUser = registerUser;
    this.authenticateUser = authenticateUser;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public RegisterUserResult register(@Valid @RequestBody RegisterUserRequest request) {
    return registerUser.execute(
        new RegisterUserCommand(request.email(), request.password(), request.displayName()));
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return AuthResponse.from(authenticateUser.execute(
        new AuthenticateUserCommand(request.email(), request.password())));
  }
}
