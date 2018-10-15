package com.testingsyndicate.hc.jmx;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HcJmxExceptionTest {

  @Test
  public void constructsWithMessage() {
    // given
    String message = "wibble";

    // when
    HcJmxException actual = new HcJmxException(message);

    // then
    assertThat(actual)
        .hasMessage(message)
        .hasNoCause();
  }

  @Test
  public void constructsWithMessageAndCause() {
    // given
    String message = "wibble";
    Throwable cause = new RuntimeException();

    // when
    HcJmxException actual = new HcJmxException(message, cause);

    // then
    assertThat(actual)
        .hasMessage(message)
        .hasCause(cause);
  }
}
