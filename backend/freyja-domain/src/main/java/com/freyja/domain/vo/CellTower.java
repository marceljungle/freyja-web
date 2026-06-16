package com.freyja.domain.vo;

import com.freyja.domain.exception.ValidationException;

/**
 * Serving-cell identity reported by the firmware when there is no GPS fix
 * (MCC, MNC, TAC and Cell ID). Used to resolve an approximate location.
 */
public record CellTower(int mcc, int mnc, int tac, int cellId) {

  public CellTower {
    if (mcc <= 0 || mnc <= 0 || tac <= 0 || cellId <= 0) {
      throw new ValidationException("Cell tower identifiers (mcc, mnc, tac, cellId) must all be positive");
    }
  }

  public static CellTower of(int mcc, int mnc, int tac, int cellId) {
    return new CellTower(mcc, mnc, tac, cellId);
  }
}
