package com.freyja.domain.exception;

public class MessagingException extends DomainException {

  public MessagingException(String message) {
    super(message);
  }

  public MessagingException(String message, Throwable cause) {
    super(message, cause);
  }
}
