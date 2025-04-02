package com.dhia.Upvertise.handler;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String keycloakId) {
    super("User with Keycloak ID " + keycloakId + " not found");
  }
}
