package com.aquinoa.lunch.controllers.advices;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.aquinoa.lunch.exceptions.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  static final Logger l = LoggerFactory.getLogger(RestExceptionHandler.class);

  /**
   * Wrapping errors into service to hide the real error from the user. Recommended to be used for
   * public APIs.
   */
  @ExceptionHandler(ServiceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public SimpleResponse handleServiceException(ServiceException ex, HttpServletResponse response) {
    return SimpleResponse.builder().errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(ex.getMessage()).build();
  }

  /**
   * In case the API service is not reachable
   */
  @ExceptionHandler(RestClientException.class)
  @ResponseStatus(HttpStatus.BAD_GATEWAY)
  public SimpleResponse handleRestClientException(RestClientException ex,
      HttpServletResponse response) {
    l.debug("Third-party client error: {}", ex.getMessage());
    return SimpleResponse.builder().errorCode(HttpStatus.BAD_GATEWAY.value())
        .message("Unable to reach Recipe/Ingredient API Service").build();
  }

  /**
   * In case the recipe/ingredient key for the API call is invalid (null)
   */
  @ExceptionHandler(NullPointerException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public SimpleResponse handleNullPointerException(NullPointerException ex,
      HttpServletResponse response) {
    return SimpleResponse.builder().errorCode(HttpStatus.BAD_REQUEST.value())
        .message("Invalid Recipe/Ingredient key").build();
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  static class SimpleResponse {
    public int errorCode;
    public String message;
  }
}
