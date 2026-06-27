package com.maybeitssquid.tin;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidTINExceptionTest {

  @Test
  void isIllegalArgumentException() {
    assertInstanceOf(IllegalArgumentException.class, new InvalidTINException("oops"));
  }

  @Test
  void messageConstructorRetainsMessage() {
    InvalidTINException ex = new InvalidTINException("bad TIN");
    assertEquals("bad TIN", ex.getMessage());
    assertNull(ex.getCause());
  }

  @Test
  void messageAndCauseConstructorRetainsBoth() {
    Throwable cause = new IllegalStateException("root cause");
    InvalidTINException ex = new InvalidTINException("bad TIN", cause);
    assertEquals("bad TIN", ex.getMessage());
    assertSame(cause, ex.getCause());
  }
}
