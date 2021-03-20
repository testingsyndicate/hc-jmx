package com.testingsyndicate.hc.jmx;

public class HcJmxException extends RuntimeException {

  HcJmxException(String message) {
    super(message);
  }

  HcJmxException(String message, Throwable cause) {
    super(message, cause);
  }
}
