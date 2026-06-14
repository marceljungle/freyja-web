package com.freyja.domain.vo;

import java.time.Instant;

public record IssuedToken(String token, Instant expiresAt) {

}
