package com.sparta.point.application.exception;

import org.springframework.http.HttpStatus;

public class NotFoundPointException extends PointException {

  public NotFoundPointException() {
    super(Error.NOT_FOUND_POINT, HttpStatus.NOT_FOUND);
  }
}
