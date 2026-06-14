package com.freyja.domain.vo;

import com.freyja.domain.exception.ValidationException;

public record BatteryLevel(int millivolts) {

  private static final int EMPTY_MV = 3300;

  private static final int FULL_MV = 4200;

  public BatteryLevel {
    if (millivolts < 0) {
      throw new ValidationException("Battery millivolts must not be negative");
    }
  }

  public static BatteryLevel ofMillivolts(int millivolts) {
    return new BatteryLevel(millivolts);
  }

  /**
   * @return estimated state of charge in the range [0, 100].
   */
  public int percentage() {
    if (millivolts <= EMPTY_MV) {
      return 0;
    }
    if (millivolts >= FULL_MV) {
      return 100;
    }
    return (int) Math.round((millivolts - EMPTY_MV) * 100.0 / (FULL_MV - EMPTY_MV));
  }
}
