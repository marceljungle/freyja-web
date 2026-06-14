package com.freyja.application.common;

/**
 * Marker for use-case inputs that can validate their own invariants. {@link AbstractUseCase} invokes {@link #validate()} before the
 * business logic runs, satisfying the requirement that input validation precedes execution.
 */
public interface UseCaseInput {

  /**
   * Validate this input. Implementations throw {@link com.freyja.domain.exception.ValidationException} on the first violated invariant.
   */
  void validate();
}
