package com.freyja.domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.freyja.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

class BatteryLevelTest {

  @Test
  void clampsAtEmptyAndFull() {
    assertEquals(0, BatteryLevel.ofMillivolts(3300).percentage());
    assertEquals(0, BatteryLevel.ofMillivolts(3000).percentage());
    assertEquals(100, BatteryLevel.ofMillivolts(4200).percentage());
    assertEquals(100, BatteryLevel.ofMillivolts(4500).percentage());
  }

  @Test
  void interpolatesLinearlyInBetween() {
    // Midpoint between 3300 and 4200 is 3750 -> ~50%.
    assertEquals(50, BatteryLevel.ofMillivolts(3750).percentage());
    assertEquals(4012, BatteryLevel.ofMillivolts(4012).millivolts());
  }

  @Test
  void rejectsNegativeMillivolts() {
    assertThrows(ValidationException.class, () -> BatteryLevel.ofMillivolts(-1));
  }
}
