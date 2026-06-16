package com.freyja.domain.vo;

/**
 * An approximate location resolved from a {@link CellTower}, with the provider's
 * estimated accuracy radius in metres.
 */
public record CellLocation(Coordinates coordinates, double accuracyMeters) {

  public static CellLocation of(Coordinates coordinates, double accuracyMeters) {
    return new CellLocation(coordinates, accuracyMeters);
  }
}
