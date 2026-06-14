package com.freyja.infrastructure.security;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String email, String role) {

}
