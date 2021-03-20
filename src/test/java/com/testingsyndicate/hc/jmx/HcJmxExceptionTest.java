package com.testingsyndicate.hc.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HcJmxExceptionTest {

  @Test
  void constructsWithMessage() {
    // given
    String message = "wibble";

    // when
    HcJmxException actual = new HcJmxException(message);

    // then
    assertThat(actual).hasMessage(message).hasNoCause();
  }

  @Test
  void constructsWithMessageAndCause() {
    // given
    String message = "wibble";
    Throwable cause = new RuntimeException();

    // when
    HcJmxException actual = new HcJmxException(message, cause);

    // then
    assertThat(actual).hasMessage(message).hasCause(cause);
  }
}
