package com.freyja.application.common;

import com.freyja.domain.exception.ValidationException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for read-only use cases (queries). Identical to {@link AbstractUseCase} but opens a read-only transaction, allowing the
 * persistence layer to optimise accordingly.
 */
public abstract class AbstractReadOnlyUseCase<I extends UseCaseInput, O> implements UseCase<I, O> {

  @Override
  @Transactional(readOnly = true)
  public O execute(I input) {
    if (input == null) {
      throw new ValidationException("Input must not be null");
    }
    input.validate();
    return handle(input);
  }

  /**
   * Query logic, executed only after the input has been validated.
   */
  protected abstract O handle(I input);
}
