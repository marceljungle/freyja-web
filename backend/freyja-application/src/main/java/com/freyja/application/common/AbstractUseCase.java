package com.freyja.application.common;

import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for read-write use cases. {@link #execute} is the transactional entry point (so the proxy can advise it) and enforces the
 * "validate-then-execute" contract before delegating to {@link #handle}.
 *
 * <p>Note: {@code execute} must stay non-final and public so the Spring AOP
 * proxy can apply the transaction advice; the validation/business logic then runs inside that transaction.
 */
public abstract class AbstractUseCase<I extends UseCaseInput, O> implements UseCase<I, O> {

  @Override
  @Transactional
  public O execute(I input) {
    if (input == null) {
      throw new com.freyja.domain.exception.ValidationException("Input must not be null");
    }
    input.validate();
    return handle(input);
  }

  /**
   * Business logic, executed only after the input has been validated.
   */
  protected abstract O handle(I input);
}
