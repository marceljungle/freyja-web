package com.freyja.domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.freyja.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

class ImeiTest {

  @Test
  void acceptsValidFifteenDigitImei() {
    assertEquals("352656100000000", Imei.of("352656100000000").value());
  }

  @Test
  void trimsWhitespace() {
    assertEquals("352656100000000", Imei.of("  352656100000000\n").value());
  }

  @Test
  void rejectsNonNumericOrWrongLength() {
    assertThrows(ValidationException.class, () -> Imei.of("not-a-number"));
    assertThrows(ValidationException.class, () -> Imei.of("12345"));
    assertThrows(ValidationException.class, () -> Imei.of(""));
  }
}
