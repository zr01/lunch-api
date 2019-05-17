package com.aquinoa.lunch.exceptions;

public class ServiceException extends Exception {

  private static final long serialVersionUID = -7489866349645423564L;

  public ServiceException(String msg) {
    super(msg);
  }
}
