package com.freyja.application.common;

/**
 * A single unit of business logic. Implementations transform a validated input into an output.
 *
 * @param <I> input type (must be self-validating)
 * @param <O> output type
 */
public interface UseCase<I extends UseCaseInput, O> {

  O execute(I input);
}
